package org.hddbscan.dbscan;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class DataSet implements Iterable<DataRow> {
	private List<String> labels = new CopyOnWriteArrayList<>();
	private List<DataRow> rowList = new CopyOnWriteArrayList<>();
	
	
	public DataSet() {
	}
	
	public void setLabels(String... labels) {
		this.labels.clear();
		Collections.addAll(this.labels, labels);
	}
	
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

	@Override
	public String toString() {
		return "DataSet [labels=" + labels + ", rowList=" + rowList + "]";
	}



}
