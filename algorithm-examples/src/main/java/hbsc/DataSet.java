package hbsc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class DataSet {
	private List<String> labels = new ArrayList<>();
	private List<DataRow> rowList = new ArrayList<>();
	
	
	public DataSet() {
	}
	
	public void setLabels(String... labels) {
		this.labels.clear();
		Collections.addAll(this.labels, labels);
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



}
