package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;


public class DataRow {
	private String id;
	private List<Number> datas = new ArrayList<>();
//	private List<Object> attrs = new ArrayList<>();
	private DataSet parent;
	
	
	public DataRow() {
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setData(Number...values) {
		this.datas.clear();
		Collections.addAll(this.datas, values);
	}
	
//	public void setAttrs(Object...values) {
//		this.attrs.clear();
//		Collections.addAll(this.attrs, values);
//	}
	
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

	@Override
	public int hashCode() {
//		return Objects.hash(attrs, datas, id);
		return Objects.hash(datas, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataRow other = (DataRow) obj;
//		return Objects.equals(attrs, other.attrs) && Objects.equals(datas, other.datas) && Objects.equals(id, other.id);
		return Objects.equals(datas, other.datas) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
//		return "DataRow [id=" + id + ", datas=" + datas + ", attrs=" + attrs + "]";
		return "DataRow [id=" + id + ", datas=" + datas + "]";
	}

	public void print(Appendable out, String delimiter) throws IOException {
		out.append(this.id).append(delimiter);
		out.append(String.join(delimiter, this.datas.stream().map((value)->String.valueOf(value.doubleValue())).toList()))
			.append('\n');
	}



}
