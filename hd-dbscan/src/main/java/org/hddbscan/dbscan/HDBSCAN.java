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
	
	private final DBSCANMetadata metadata;
	
	
	public HDBSCAN() {
		this.metadata = new DBSCANMetadata();
	}
	
	public DBSCANMetadata getMetadata() {
		return this.metadata;
	}
	
	public DBSCANModel fit(final DataSet inputValues) {
		ExecutorService excSvc = Executors.newFixedThreadPool(3);

		int colCnt = inputValues.getColumnCount();
		int minPts = this.metadata.getMinPts();
		final DBSCAN dbscan = new DBSCAN();
		
		List<Callable<List<DBSCANCluster>>> hd = new ArrayList<>();
		for(int i = 0 ; i < colCnt; i++) {
			final int colIdx = i;
			final double eps = this.metadata.getEps(i);
			
			hd.add(()-> {
				List<DBSCANCluster> clusterList = dbscan.fit(inputValues, colIdx, eps, minPts);
				return clusterList;
			});
		}
		
		try {
			List<Future<List<DBSCANCluster>>> result = excSvc.invokeAll(hd);
			
			excSvc.shutdown();
			excSvc.awaitTermination(2L, TimeUnit.MINUTES);
			
			final DBSCANModelBuilder modelBuilder = new DBSCANModelBuilder();
			
			result.forEach((Future<List<DBSCANCluster>> clusterList) -> {
				try {
					List<DBSCANCluster> clusters = clusterList.get();
					modelBuilder.add(clusters);
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
			log.error("InterruptedException", e);
			
			throw new RuntimeException(e);
		}
		
		
	}
	
	
	

}
