package org.hddbscan.dbscan.feature;

import java.util.Map;

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

	@Override
	protected void toMap(Map<String, Object> map) {
		map.put("eps", this.eps);
	}
	
	public static DimensionConstraint fromMap(int minPts, Map<String, Object> map) {
		if(map.get("eps") instanceof Double) {
			double eps = (Double)map.get("eps");
			return new DoubleConstraint(minPts, eps);
		}
		return null;
	}


}
