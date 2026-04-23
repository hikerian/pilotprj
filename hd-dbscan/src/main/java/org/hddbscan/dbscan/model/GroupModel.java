package org.hddbscan.dbscan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hddbscan.dbscan.DBSCANModel.DBSCANGroup;
import org.hddbscan.dbscan.DBSCANRange;
import org.hddbscan.dbscan.DataRow;


public class GroupModel {
	private String id;
	private String label;
	private List<RangeModel> rangeList;
	private List<DataRowModel> dataList;
	
	
	public GroupModel() {
	}
	
	public GroupModel(DBSCANGroup group) {
		this.id = group.getId();
		this.label = group.getLabel();
		
		this.rangeList = new ArrayList<>();
		List<DBSCANRange> rangeList = group.getRangeList();
		for(DBSCANRange range : rangeList) {
			this.rangeList.add(new RangeModel(range));
		}
		
		this.dataList = new ArrayList<>();
		List<DataRow> dataList = group.getDataList();
		for(DataRow dataRow : dataList) {
			this.dataList.add(new DataRowModel(dataRow));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<RangeModel> getRangeList() {
		return rangeList;
	}

	public void setRangeList(List<RangeModel> rangeList) {
		this.rangeList = rangeList;
	}

	public List<DataRowModel> getDataList() {
		return dataList;
	}

	public void setDataList(List<DataRowModel> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataList, id, label, rangeList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupModel other = (GroupModel) obj;
		return Objects.equals(dataList, other.dataList) && Objects.equals(id, other.id)
				&& Objects.equals(label, other.label) && Objects.equals(rangeList, other.rangeList);
	}
	
	

}
