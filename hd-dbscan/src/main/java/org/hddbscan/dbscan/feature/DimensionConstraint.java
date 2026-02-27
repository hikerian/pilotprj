package org.hddbscan.dbscan.feature;

import org.hddbscan.dbscan.HDBSCAN.Neighbours;


public abstract class DimensionConstraint {
	private final int minPts;
	
	
	protected DimensionConstraint(int minPts) {
		this.minPts = minPts;
	}
	
	public int getMinPts() {
		return this.minPts;
	}

	public boolean isAcceptableNeighbours(Neighbours neighbours) {
		return neighbours.size() >= this.minPts;
	}
	
	public abstract double[] getEps();
	public abstract boolean isNeighbours(ComputableFeature a, ComputableFeature b);


}
