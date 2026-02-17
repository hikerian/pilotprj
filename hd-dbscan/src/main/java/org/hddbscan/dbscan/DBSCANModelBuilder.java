package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBSCANModelBuilder {
	private final Logger log = LoggerFactory.getLogger(DBSCANModelBuilder.class);
	
	private List<String> labels = new CopyOnWriteArrayList<>();
	private final List<List<DBSCANCluster>> clusterList; 
	private DBSCANMetadata metadata;
	
	
	
	public DBSCANModelBuilder() {
		this.clusterList = new ArrayList<>();
	}
	
	public DBSCANModelBuilder(int colCnt) {
		this.clusterList = new ArrayList<>(colCnt);
		for(int i = 0; i < colCnt; i++) {
			this.clusterList.add(null);
		}
	}
	
	public DBSCANModelBuilder setLabels(List<String> labelList) {
		this.labels.addAll(labelList);
		
		return this;
	}
	
	public DBSCANModelBuilder setMetadata(DBSCANMetadata metadata) {
		this.metadata = metadata;
		
		return this;
	}
	
	public DBSCANModelBuilder add(List<DBSCANCluster> clusterList) {
		this.clusterList.add(clusterList);

		return this;
	}
	
	public DBSCANModelBuilder set(int colIdx, List<DBSCANCluster> clusterList) {
		this.log.info("colIdx: {}, clusterSize: {}", colIdx, clusterList.size());
		
		this.clusterList.set(colIdx, clusterList);

		return this;
	}
	
//	public DBSCANModel build_backup() {
//		DBSCANModel model = new DBSCANModel();
//		model.setLabels(this.labels);
//		
//		/*
//		 * 각 컬럼별 클러스터에서 교집합을 찾음
//		 */
//		// for firstColumn Cluster
//		List<DBSCANCluster> rootCluster = new ArrayList<>();
//		
//		List<DBSCANCluster> clusterM = this.clusterList.get(0);
//		rootCluster.addAll(clusterM);
//		for(DBSCANCluster cluster : clusterM) {
//			List<DBSCANCluster> parentCluster = new ArrayList<>();
//			parentCluster.add(cluster);
//			
//			for(int i = 1; i < this.clusterList.size(); i++) {
//				List<DBSCANCluster> clusterList = this.clusterList.get(i);
//				
//				for(DBSCANCluster other : clusterList) {
//					
//					for(DBSCANCluster parent : parentCluster) {
//						parent.addIntersectionChild(other);
//					}
//				}
//				
//				List<DBSCANCluster> newParents = new ArrayList<>();
//				for(DBSCANCluster parent : parentCluster) {
//					newParents.addAll(parent.getChildren());
//				}
//				parentCluster = newParents;
//			}
//		}
//		
//		int columnSize = this.clusterList.size();
//		for(DBSCANCluster cluster : rootCluster) {
//			List<List<DBSCANCluster>> clusters = cluster.flat(columnSize);
//			if(clusters != null && clusters.size() > 0) {
//				
//				for(List<DBSCANCluster> modelGroup : clusters) {
//					model.addGroup(modelGroup);
//				}
//			}
//		}
//		
//
//		return model;
//	}
	
	public DBSCANModel build() {
		DBSCANModel model = new DBSCANModel();
		model.setMetadata(this.metadata);
		model.setLabels(this.labels);
		
		final int colCnt = this.labels.size();
		
		/*
		 * 각 컬럼별 클러스터에서 교집합을 찾음
		 */
		List<DBSCANCluster> rootClusterList = this.clusterList.get(0);
		for(DBSCANCluster rootCluster : rootClusterList) {
			List<DBSCANCluster> newClusterList = this.joinCluster(rootCluster, this.clusterList, 1, colCnt);
			
			if(newClusterList.size() > 0) {
				for(DBSCANCluster cluster : newClusterList) {
					model.addGroup(cluster);
				}
			}
		}

		return model;
	}
	
	private List<DBSCANCluster> joinCluster(DBSCANCluster parentCluster, List<List<DBSCANCluster>> allClusterList, int depth, int colCnt) {
		// intersection
		int nextDepth = depth + 1;
		List<DBSCANCluster> childClusterList = allClusterList.get(depth);
		
		this.log.info("init depth({}) cluster count {}", depth, childClusterList.size());
		
		List<DataRow> currentRowList = new ArrayList<>();
		for(DBSCANCluster childCluster : childClusterList) {
			DBSCANCluster newCluster = parentCluster.intersection(childCluster);
			if(newCluster != null) {
				currentRowList.addAll(newCluster.getDataList());
			}
		}

		List<DBSCANCluster> result = new ArrayList<>();
		if(currentRowList.size() == 0) {
			return result;
		}

		// cluster rebuild
		double eps = this.metadata.getEps(depth);
		int minPts = this.metadata.getMinPts();
		
		DBSCAN dbScan = new DBSCAN();
		childClusterList = dbScan.fit(currentRowList, depth, eps, minPts);
		
		this.log.info("rebuild depth({}) cluster count {}", depth, childClusterList.size());

		// join
		for(DBSCANCluster newCluster : childClusterList) {
			if(nextDepth < colCnt) {
				List<DBSCANCluster> newClusterList = this.joinCluster(newCluster, allClusterList, nextDepth, colCnt);
				if(newClusterList.size() > 0) {
					result.addAll(newClusterList);
				}
			} else {
				result.add(newCluster);
			}			
		}

		return result;
		
	}
	
//	private List<DBSCANCluster> joinCluster(DBSCANCluster parentCluster, List<List<DBSCANCluster>> allClusterList, int depth, int colCnt) {
//		List<DBSCANCluster> result = new ArrayList<>();
//		
//		int nextDepth = depth + 1;
//		List<DBSCANCluster> childClusterList = allClusterList.get(depth);
//		for(DBSCANCluster childCluster : childClusterList) {
//			DBSCANCluster newCluster = parentCluster.intersection(childCluster);
//			if(newCluster != null) {
//				if(nextDepth < colCnt) {
//					List<DBSCANCluster> newClusterList = this.joinCluster(newCluster, allClusterList, nextDepth, colCnt);
//					if(newClusterList.size() > 0) {
//						result.addAll(newClusterList);
//					}
//				} else {
//					result.add(newCluster);
//				}
//			}
//		}
//		return result;
//		
//	}
	
	

}
