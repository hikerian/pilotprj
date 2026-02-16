package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBSCANModel {
	private final Logger log = LoggerFactory.getLogger(DBSCANModel.class);
	
	
	public static class DBSCANGroup {
		private String id;
		private List<DBSCANRange> rangeList = new ArrayList<>();
		private List<DataRow> dataList;
		
		
		public DBSCANGroup(String id) {
			this.id = id;
		}
		
		public void addRange(DBSCANRange range) {
			this.rangeList.add(range);
		}
		
		public void setDataRowList(List<DataRow> dataList) {
			this.dataList = dataList;
		}

		@Override
		public String toString() {
			return "DBSCANGroup [id=" + id + ", rangeList=" + rangeList + "\n, dataList=" + dataList + "]\n\n";
//			return "DBSCANGroup [id=" + id + ", rangeList=" + rangeList + "]\n";
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
		this.log.debug("ColSize: {}", colSize);
		
		for(DBSCANCluster cluster : groupCluster) {
			DBSCANGroup group = new DBSCANGroup("group-" + (this.groups.size() + 1));
			this.groups.add(group);
			
			for(int i = 0; i < colSize; i++) {
				DBSCANRange range = cluster.getRange(i);
				group.addRange(range);
			}
			
			group.setDataRowList(new ArrayList<>(cluster.getDataList()));
		}
	}
	
	public int getGroupCount() {
		return this.groups.size();
	}

	@Override
	public String toString() {
		return "DBSCANModel [labels=" + labels + ", groups=\n" + groups + "]";
	}


}
