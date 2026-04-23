package org.hddbscan.dbscan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hddbscan.dbscan.DBSCANModel;
import org.hddbscan.dbscan.DBSCANModel.DBSCANGroup;
import org.hddbscan.dbscan.feature.DimensionConstraint;


public class DBSCANGroupModel {
	private List<String> labels;
	private Map<String, Object>[] epsList;
	private List<GroupModel> groups;
	
	
	public DBSCANGroupModel() {
	}
	
	@SuppressWarnings("unchecked")
	public DBSCANGroupModel(DBSCANModel model) {
		this.labels = new ArrayList<>(model.getLabels());
		
		DimensionConstraint[] epsList = model.getEpsList();
		this.epsList = new Map[epsList.length];
		for(int i = 0; i < epsList.length; i++) {
			this.epsList[i] = epsList[i].toMap();
		}
		
		this.groups = new ArrayList<>();
		List<DBSCANGroup> groups = model.getGroups();
		for(DBSCANGroup group : groups) {
			this.groups.add(new GroupModel(group));
		}
		
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Map<String, Object>[] getEpsList() {
		return epsList;
	}

	public void setEpsList(Map<String, Object>[] rpsList) {
		this.epsList = rpsList;
	}

	public List<GroupModel> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupModel> groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(epsList);
		result = prime * result + Objects.hash(groups, labels);
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
		DBSCANGroupModel other = (DBSCANGroupModel) obj;
		return Objects.equals(groups, other.groups) && Objects.equals(labels, other.labels)
				&& Arrays.equals(epsList, other.epsList);
	}


}
