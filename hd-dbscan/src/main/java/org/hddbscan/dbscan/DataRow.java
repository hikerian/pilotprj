package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.hddbscan.dbscan.feature.ComputableFeature;
import org.hddbscan.dbscan.model.DataRowModel;


public class DataRow {
	private String id;
	private ComputableFeature[] datas;
	
	
	public DataRow() {
	}
	
	public DataRow(DataRowModel rowModel) {
		this.id = rowModel.getId();
		Map<String, Object>[] datas = rowModel.getDatas();
		this.datas = new ComputableFeature[datas.length];
		
		for(int i = 0; i < datas.length; i++) {
			this.datas[i] = ComputableFeature.fromMap(datas[i]);
		}
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setData(ComputableFeature...values) {
		this.datas = values;
	}
	
	public ComputableFeature[] getDatas() {
		return this.datas;
	}
	
	public ComputableFeature getData(int index) {
		return this.datas[index];
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.datas);
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
		return Objects.equals(this.id, other.id) && Objects.equals(this.datas, other.datas);
	}
	
	@Override
	public String toString() {
		return "DataRow [id=" + this.id + ", datas=[" + String.join(",", Arrays.stream(this.datas).map((value)->value.toString()).toList()) + "]]";
	}

	public void print(Appendable out, String delimiter) throws IOException {
		out.append(this.id).append(delimiter);
		out.append(
				String.join(delimiter, Arrays.stream(this.datas).map(
						(value)->value.toString()
						).toList())
			).append('\n');
	}


}
