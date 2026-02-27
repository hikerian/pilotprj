package org.hddbscan.dbscan.feature;


public class DoubleConstraint extends DimensionConstraint {
	private final double eps;
	
	
	public DoubleConstraint(int minPts, double eps) {
		super(minPts);
		this.eps = eps;
	}

	@Override
	public double[] getEps() {
		return new double[] {this.eps};
	}
	
	@Override
	public boolean isNeighbours(ComputableFeature a, ComputableFeature b) {
		DoubleFeature da = (DoubleFeature)a;
		DoubleFeature db = (DoubleFeature)b;
		
		double distance = Math.abs(da.getValue() - db.getValue());

		return distance <= this.eps;
	}


}
