package org.hddbscan.dbscan.backup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DBSCANCluster {
	private final List<DataRow> dataList;
//	private final List<DBSCANCluster> children;
	
	
	public DBSCANCluster(List<DataRow> neighbours) {
		this.dataList = neighbours;
//		this.children = new ArrayList<>();
	}
	
//	public void addIntersectionChild(DBSCANCluster otherCluster) {
//		DBSCANCluster child = this.intersection(otherCluster);
//		if(child != null) {
//			this.children.add(child);
//		}
//	}
	
	public List<DataRow> getDataList() {
		return this.dataList;
	}
	
//	public List<DBSCANCluster> getChildren() {
//		return this.children;
//	}
	
	public DBSCANCluster intersection(DBSCANCluster otherCluster) {
		List<DataRow> intersection = new ArrayList<>();
		
		for(DataRow otherRow : otherCluster.dataList) {
			if(this.dataList.contains(otherRow)) {
				intersection.add(otherRow);
			}
		}
		
		if(intersection.size() == 0) {
			return null;
		}
		
		return new DBSCANCluster(intersection);
	}
	
//	public List<List<DBSCANCluster>> flat(int size) {
//		List<DBSCANCluster> clusters = new ArrayList<>();
//		List<List<DBSCANCluster>> clusterList = this.flat(clusters);
//		
//		List<List<DBSCANCluster>> result = new ArrayList<>();
//		for(List<DBSCANCluster> cluster : clusterList) {
//			if(cluster.size() == size) {
//				result.add(cluster);
//			}
//		}
//		
//		return result;
//	}
	
//	public List<List<DBSCANCluster>> flat(List<DBSCANCluster> clusters) {
//		clusters.add(this);
//
//		List<List<DBSCANCluster>> clusterList = new ArrayList<>();
//		if(this.children.size() == 0) {
//			clusterList.add(clusters);
//		} else {
//			for(DBSCANCluster child : this.children) {
//				List<DBSCANCluster> childCluster = new ArrayList<>(clusters);
//				List<List<DBSCANCluster>> childClusterList = child.flat(childCluster);
//				
//				childClusterList.forEach((List<DBSCANCluster> childClusters)-> {
//					clusterList.add(childClusters);
//				});
//			}
//		}
//
//		return clusterList;
//	}
	
	public DBSCANRange getRange(int col) {
		double min = -1D;
		double max = -1D;
		
		for(DataRow row : this.dataList) {
			Number value = row.getData(col);
			double doubleValue = value.doubleValue();
			
			min = (min == -1D ? doubleValue : (min > doubleValue ? doubleValue : min));
			max = (max == -1D ? doubleValue : (max < doubleValue ? doubleValue : max));
		}
		
		return new DBSCANRange(min, max);
	}
	

	@Override
	public String toString() {
		return "DBSCANCluster [dataList=" + this.dataList + "]";
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append("DBSCANCluster:\n");
		for(DataRow row : this.dataList) {
			row.print(out, delimiter);
		}
	}


}
