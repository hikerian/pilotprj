package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hddbscan.dbscan.feature.Distance;


public class DBSCANMetadata {
	private int minPts = 4;
	private List<Distance> epsList = new ArrayList<>();
	
	
	public DBSCANMetadata() {
	}
	
	public int getMinPts() {
		return this.minPts;
	}
	
	public void setMinPts(int minPts) {
		this.minPts = minPts;
	}
	
	public void setEpsList(Distance...epsList) {
		this.epsList.clear();
		Collections.addAll(this.epsList, epsList);
	}
	
	public void addEps(Distance eps) {
		this.epsList.add(eps);
	}
	
	public void setEps(int index, Distance eps) {
		this.epsList.set(index, eps);
	}
	
	public int getEpsCount() {
		return this.epsList.size();
	}
	
	public Distance getEps(int index) {
		return this.epsList.get(index);
	}



}
