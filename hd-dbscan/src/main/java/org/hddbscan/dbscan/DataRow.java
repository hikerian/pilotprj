package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class DataRow {
	private String id;
	private List<Number> datas = new ArrayList<>();
	private DataSet parent;
	
	
	public DataRow() {
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setData(Number... values) {
		this.datas.clear();
		Collections.addAll(this.datas, values);
	}
	
	public void setData(Map<String, Number> valueSet) {
		Set<Entry<String, Number>> entrySet = valueSet.entrySet();
		for(Entry<String, Number> entry : entrySet) {
			String label = entry.getKey();
			Number value = entry.getValue();
			this.setData(label, value);
		}
	}
	
	public void setData(String label, Number value) {
		int index = this.parent.getLabelIndex(label);
		this.datas.set(index, value);
	}
	
	public Number getData(String label) {
		int index = this.parent.getLabelIndex(label);
		return this.datas.get(index);
	}
	
	public int getColumnCount() {
		return this.datas.size();
	}
	
	public Number getData(int index) {
		return this.datas.get(index);
	}
	
	protected void setParent(DataSet parent) {
		this.parent = parent;
	}
	

}
