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
	
	
	
	public DBSCANModelBuilder() {
		this.clusterList = new ArrayList<>();
	}
	
	public void setLabels(List<String> labelList) {
		this.labels.addAll(labelList);
	}
	
	public DBSCANModelBuilder add(List<DBSCANCluster> clusterList) {
		this.clusterList.add(clusterList);

		return this;
	}
	
	public DBSCANModel build() {
		DBSCANModel model = new DBSCANModel();
		model.setLabels(this.labels);
		
		/*
		 * 각 컬럼별 클러스터에서 교집합을 찾음
		 */
		// for firstColumn Cluster
		List<DBSCANCluster> rootCluster = new ArrayList<>();
		
		List<DBSCANCluster> clusterM = this.clusterList.get(0);
		rootCluster.addAll(clusterM);
		for(DBSCANCluster cluster : clusterM) {
			List<DBSCANCluster> parentCluster = new ArrayList<>();
			parentCluster.add(cluster);
			
			for(int i = 1; i < this.clusterList.size(); i++) {
				List<DBSCANCluster> clusterList = this.clusterList.get(i);
				
				for(DBSCANCluster other : clusterList) {
					
					for(DBSCANCluster parent : parentCluster) {
						parent.addIntersectionChild(other);
					}
				}
				
				List<DBSCANCluster> newParents = new ArrayList<>();
				for(DBSCANCluster parent : parentCluster) {
					newParents.addAll(parent.getChildren());
				}
				parentCluster = newParents;
			}
		}
		
		int columnSize = this.clusterList.size();
		for(DBSCANCluster cluster : rootCluster) {
			List<List<DBSCANCluster>> clusters = cluster.flat(columnSize);
			if(clusters != null && clusters.size() > 0) {
				
				for(List<DBSCANCluster> modelGroup : clusters) {
					model.addGroup(modelGroup);
				}
			}
		}
		

		return model;
	}
	
	

}
