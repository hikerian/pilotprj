package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DBSCANMetadata {
	private Double DEFAULT_EPS = 1.0D;
	private int minPts = 4;
	private List<Double> epsList = new ArrayList<>();
	
	
	public DBSCANMetadata() {
	}
	
	public int getMinPts() {
		return this.minPts;
	}
	
	public void setMinPts(int minPts) {
		this.minPts = minPts;
	}
	
	public void setEpsList(Double...epsList) {
		this.epsList.clear();
		Collections.addAll(this.epsList, epsList);
	}
	
	public void addEps(Double eps) {
		this.epsList.add(eps);
	}
	
	public void setEps(int index, Double eps) {
		this.epsList.set(index, eps);
	}
	
	public int getEpsCount() {
		return this.epsList.size();
	}
	
	public Double getEps(int index) {
		if(this.epsList.size() <= index) {
			return this.DEFAULT_EPS;
		}
		return this.epsList.get(index);
	}



}
