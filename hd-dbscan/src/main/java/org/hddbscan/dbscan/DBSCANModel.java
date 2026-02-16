package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBSCANModel {
	private final Logger log = LoggerFactory.getLogger(DBSCANModel.class);
	
	
	public static class DBSCANGroup {
		private List<DBSCANRange> rangeList = new ArrayList<>();
		
		
		public DBSCANGroup() {
		}
		
		public void addRange(DBSCANRange range) {
			this.rangeList.add(range);
		}
		
	}

	
	private List<String> labels = new ArrayList<>();
	private List<DBSCANGroup> groups = new ArrayList<>();
	
	
	public DBSCANModel() {
	}
	
	public void setLabels(List<String> labelList) {
		this.labels.addAll(labelList);
	}
	
	public void addGroup(List<DBSCANCluster> groupCluster) {
		final int colSize = this.labels.size();
		
		for(DBSCANCluster cluster : groupCluster) {
			DBSCANGroup group = new DBSCANGroup();
			this.groups.add(group);
			
			for(int i = 0; i < colSize; i++) {
				DBSCANRange range = cluster.getRange(i);
				group.addRange(range);
			}
		}
	}


}
