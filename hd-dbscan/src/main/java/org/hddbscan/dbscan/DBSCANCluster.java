package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.List;

import org.hddbscan.dbscan.feature.ComputableFeature;


public class DBSCANCluster {
	private final List<DataRow> dataList;
	
	
	public DBSCANCluster(List<DataRow> neighbours) {
		this.dataList = neighbours;
	}
	
	public List<DataRow> getDataList() {
		return this.dataList;
	}
	
	public DBSCANRange getRange(int col) {
		ComputableFeature min = null;
		ComputableFeature max = null;
		for(DataRow row : this.dataList) {
			ComputableFeature value = row.getData(col);
			
			if(min == null && max == null) {
				min = value.clone();
				max = value.clone();
				continue;
			} else {
				min = value.min(min);
				max = value.max(max);
			}
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
