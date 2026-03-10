package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//public class DataSet implements Iterable<DataRow> {
public class DataSet {
//	private List<String> labels = new CopyOnWriteArrayList<>();
//	private List<DataRow> rowList = new CopyOnWriteArrayList<>();
	private List<String> labels = new ArrayList<>();
	private List<DBSCANCluster> clusterList = new ArrayList<>();
	
	
	public DataSet() {
	}
	
	public void setLabels(String... labels) {
		this.labels.clear();
		Collections.addAll(this.labels, labels);
	}
	
	public void addLabel(String label) {
		this.labels.add(label);
	}
	
	public List<String> getLabels() {
		return this.labels;
	}
	
	public void removeAllLabels() {
		this.labels.clear();
	}
	
	public void addCluster(DBSCANCluster cluster) {
		if(this.clusterList.contains(cluster) == false) {
			this.clusterList.add(cluster);
		}
	}
	
	public List<DBSCANCluster> getClusters() {
		return this.clusterList;
	}
	
//	public void addRow(DataRow row) {
//		this.rowList.add(row);
//	}
	
//	public void addAllRow(Collection<DataRow> rows) {
//		for(DataRow row : rows) {
//			this.addRow(row);
//		}
//	}
	
//	public List<DataRow> getAllRows() {
//		return this.rowList;
//	}
	
	public int getColumnCount() {
		return this.labels.size();
	}
	
	public int getLabelIndex(String label) {
		return this.labels.indexOf(label);
	}
	
//	public void removeAllRows() {
//		this.rowList.clear();
//	}
//	
//	public int getRowCount() {
//		return this.rowList.size();
//	}
//	
//	public DataRow getRow(int idx) {
//		return this.rowList.get(idx);
//	}
//	
//	public void shuffle() {
//		Collections.shuffle(this.rowList);
//	}

//	@Override
//	public Iterator<DataRow> iterator() {
//		return this.rowList.iterator();
//	}
	
//	public void print(Appendable out, String delimiter) throws IOException {
//		out.append("DataSet:\n");
//		out.append("id").append(delimiter).append(String.join(delimiter, this.labels)).append('\n');
//		for(DataRow dataRow : this.rowList) {
//			dataRow.print(out, delimiter);
//		}
//	}
//
//	@Override
//	public String toString() {
//		return "DataSet [labels=" + this.labels + ", rowList=" + this.rowList + "]";
//	}



}
