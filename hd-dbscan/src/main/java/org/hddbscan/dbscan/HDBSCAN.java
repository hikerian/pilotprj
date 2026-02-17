package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDBSCAN {
	private final Logger log = LoggerFactory.getLogger(HDBSCAN.class);
	
	
	private static class ColumnCluster {
		private final int colIdx;
		private final List<DBSCANCluster> clusterList;
		
		
		ColumnCluster(int colIdx, List<DBSCANCluster> clusterList) {
			this.colIdx = colIdx;
			this.clusterList = clusterList;
		}
		
		public int getColIdx() {
			return this.colIdx;
		}
		
		public List<DBSCANCluster> getCluster() {
			return this.clusterList;
		}
		
	}
	
	
	private DBSCANMetadata metadata;
	
	
	public HDBSCAN() {
		this.metadata = new DBSCANMetadata();
	}
	
	public void setMetadata(DBSCANMetadata metadata) {
		this.metadata = metadata;
	}
	
	public DBSCANMetadata getMetadata() {
		return this.metadata;
	}
	
	public DBSCANModel fit(final DataSet inputValues) {
		// TODO 1depth 군집화 -> 군집화된 클러스터에서 2depth 군집화 -> 이렇게 되면 모든 요소가 군집을 이루게 되는 문제를 고려해야 함.
		
		ExecutorService excSvc = Executors.newFixedThreadPool(3);

		int colCnt = inputValues.getColumnCount();
		int minPts = this.metadata.getMinPts();
		
		this.log.info("ColCnt: {}, MinPts: {}", colCnt, minPts);
		
		final DBSCAN dbscan = new DBSCAN();
		
		List<Callable<ColumnCluster>> hd = new ArrayList<>();
		for(int i = 0 ; i < colCnt; i++) {
			final int colIdx = i;
			final double eps = this.metadata.getEps(i);
			
			hd.add(()-> {
				List<DBSCANCluster> clusterList = dbscan.fit(inputValues, colIdx, eps, minPts);
				
				log.info("callable: colIdx: {}, eps: {}, clusterSize: {}", colIdx, eps, clusterList.size());

				return new ColumnCluster(colIdx, clusterList);
			});
		}
		
		try {
			List<Future<ColumnCluster>> result = excSvc.invokeAll(hd);
			
			excSvc.shutdown();
			excSvc.awaitTermination(2L, TimeUnit.MINUTES);
			
			final DBSCANModelBuilder modelBuilder = new DBSCANModelBuilder(colCnt);
			modelBuilder.setLabels(inputValues.getLabels())
				.setMetadata(this.metadata);
			
			result.forEach((Future<ColumnCluster> clusterList) -> {
				try {
					ColumnCluster columnCluster = clusterList.get();
					modelBuilder.set(columnCluster.getColIdx(), columnCluster.getCluster());
				} catch (ExecutionException e) {
					log.error("ExecutionException", e);
				} catch (InterruptedException e) {
					Thread.interrupted();
					log.error("InterruptedException", e);
				}
			});
			
			DBSCANModel model = modelBuilder.build();
			
			return model;
			
		} catch (InterruptedException e) {
			Thread.interrupted();
			this.log.error("InterruptedException", e);
			
			throw new RuntimeException(e);
		}
		
		
	}
	
	
	

}
