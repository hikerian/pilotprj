package org.hddbscan.dbscan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DBSCANCluster {
	private Set<DataRow> dataList = new HashSet<>();
	
	
	public DBSCANCluster(List<DataRow> neighbours) {
		this.dataList = new HashSet<>(neighbours);
	}


	@Override
	public String toString() {
		return "DBSCANCluster [dataList=" + dataList + "]";
	}


}
