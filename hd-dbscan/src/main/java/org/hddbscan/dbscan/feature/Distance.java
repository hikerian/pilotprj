package org.hddbscan.dbscan.feature;


public interface Distance {
	public double distance(ComputableFeature a, ComputableFeature b);
	public double[] getEps();
	public boolean isNeighbours(ComputableFeature a, ComputableFeature b);

}
