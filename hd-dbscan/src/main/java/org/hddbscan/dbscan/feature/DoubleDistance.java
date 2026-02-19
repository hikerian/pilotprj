package org.hddbscan.dbscan.feature;


public class DoubleDistance implements Distance {
	private final double eps;
	
	
	public DoubleDistance(double eps) {
		this.eps = eps;
	}

	@Override
	public double distance(ComputableFeature a, ComputableFeature b) {
		DoubleFeature da = (DoubleFeature)a;
		DoubleFeature db = (DoubleFeature)b;
		
		return Math.abs(da.getValue() - db.getValue());
	}

	@Override
	public double[] getEps() {
		return new double[] {this.eps};
	}

	@Override
	public boolean isNeighbours(ComputableFeature a, ComputableFeature b) {
		double distance = this.distance(a, b);

		return distance <= this.eps;
	}

}
