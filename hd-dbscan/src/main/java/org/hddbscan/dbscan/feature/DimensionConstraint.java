package org.hddbscan.dbscan.feature;

import java.util.HashMap;
import java.util.Map;

import org.hddbscan.dbscan.HDBSCAN.Neighbours;


public abstract class DimensionConstraint {
	private int minPts;
	
	
	protected DimensionConstraint(int minPts) {
		this.minPts = minPts;
	}
	
	public int getMinPts() {
		return this.minPts;
	}

	public boolean isAcceptableNeighbours(Neighbours neighbours) {
		return neighbours.size() >= this.minPts;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("minPts", this.minPts);
		
		this.toMap(map);
		
		return map;
	}
	protected abstract void toMap(Map<String, Object> map);
	
	public static DimensionConstraint fromMap(Map<String, Object> map) {
		int minPts = (Integer)map.get("minPts");
		
		DimensionConstraint constraint = DoubleConstraint.fromMap(minPts, map);
		if(constraint == null) {
			constraint = PositionManhattanConstraint.fromMap(minPts, map);
			
			if(constraint == null) {
				return constraint;
			}
		}
		
		return constraint;
	}
	
	public abstract double[] getEps();
	public abstract boolean isNeighbours(ComputableFeature a, ComputableFeature b);


}
