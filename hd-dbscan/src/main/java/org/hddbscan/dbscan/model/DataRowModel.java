package org.hddbscan.dbscan.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.feature.ComputableFeature;


public class DataRowModel {
	private String id;
	private Map<String, Object>[] datas;
	
	
	public DataRowModel() {
	}
	
	@SuppressWarnings("unchecked")
	public DataRowModel(DataRow dataRow) {
		this.id = dataRow.getId();
		ComputableFeature[] datas = dataRow.getDatas();
		
		this.datas = new Map[datas.length];
		
		for(int i = 0; i < datas.length; i++) {
			this.datas[i] = datas[i].toMap();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object>[] getDatas() {
		return datas;
	}

	public void setDatas(Map<String, Object>[] datas) {
		this.datas = datas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(datas);
		result = prime * result + Objects.hash(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataRowModel other = (DataRowModel) obj;
		return Arrays.equals(datas, other.datas) && Objects.equals(id, other.id);
	}

	

}
