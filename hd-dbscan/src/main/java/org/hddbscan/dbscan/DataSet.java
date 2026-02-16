package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class DataSet implements Iterable<DataRow> {
	private List<String> labels = new CopyOnWriteArrayList<>();
//	private List<String> attrLabels = new CopyOnWriteArrayList<>();
	private List<DataRow> rowList = new CopyOnWriteArrayList<>();
	
	
	public DataSet() {
	}
	
	public void setLabels(String... labels) {
		this.labels.clear();
		Collections.addAll(this.labels, labels);
	}
	
//	public void setAttrLabels(String...labels) {
//		this.attrLabels.clear();
//		Collections.addAll(this.attrLabels, labels);
//	}
	
	public List<String> getLabels() {
		return this.labels;
	}
	
	public void removeAllLabels() {
		this.labels.clear();
	}
	
	public void addRow(DataRow row) {
		row.setParent(this);
		this.rowList.add(row);
	}
	
	public void addAllRow(Collection<DataRow> rows) {
		for(DataRow row : rows) {
			this.addRow(row);
		}
	}
	
	public int getColumnCount() {
		return this.labels.size();
	}
	
	public int getLabelIndex(String label) {
		return this.labels.indexOf(label);
	}
	
	public void removeAllRows() {
		this.rowList.clear();
	}
	
	public int getRowCount() {
		return this.rowList.size();
	}
	
	public DataRow getRow(int idx) {
		return this.rowList.get(idx);
	}
	
	public void shuffle() {
		Collections.shuffle(this.rowList);
	}

	@Override
	public Iterator<DataRow> iterator() {
		return this.rowList.iterator();
	}
	
	public void print(Appendable out) throws IOException {
		out.append("DataSet:\n");
		out.append("id,").append(String.join(",", this.labels)).append('\n');
		for(DataRow dataRow : this.rowList) {
			dataRow.print(out);
		}
	}

	@Override
	public String toString() {
//		return "DataSet [labels=" + labels + ", attrLabels=" + attrLabels + ", rowList=" + rowList + "]";
		return "DataSet [labels=" + labels + ", rowList=" + rowList + "]";
	}




}
