package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.List;


public class DBSCANCluster {
	private final List<DataRow> dataList;
	
	
	public DBSCANCluster(List<DataRow> neighbours) {
		this.dataList = neighbours;
	}
	
	public List<DataRow> getDataList() {
		return this.dataList;
	}
	
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
