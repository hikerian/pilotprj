package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hddbscan.dbscan.feature.DimensionConstraint;


public class DBSCANMetadata {
	private List<DimensionConstraint> constraintList = new ArrayList<>();
	
	
	public DBSCANMetadata() {
	}
	
	public void setEpsList(DimensionConstraint...constraintList) {
		this.constraintList.clear();
		Collections.addAll(this.constraintList, constraintList);
	}
	
	public void addConstraint(DimensionConstraint eps) {
		this.constraintList.add(eps);
	}
	
	public void setConstraint(int index, DimensionConstraint eps) {
		this.constraintList.set(index, eps);
	}
	
	public int getConstraintCount() {
		return this.constraintList.size();
	}
	
	public DimensionConstraint getConstraint(int index) {
		return this.constraintList.get(index);
	}


}
