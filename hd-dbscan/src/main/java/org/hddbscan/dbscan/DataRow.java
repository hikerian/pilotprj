package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.hddbscan.dbscan.feature.ComputableFeature;


public class DataRow {
	private String id;
	private ComputableFeature[] datas;
	
	
	public DataRow() {
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
	
	public ComputableFeature getData(int index) {
		return this.datas[index];
	}

	@Override
	public int hashCode() {
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
		return Objects.equals(datas, other.datas) && Objects.equals(id, other.id);
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
