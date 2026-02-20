package org.hddbscan.dbscan.feature;


public class PositionManhattanConstraint extends DimensionConstraint {
	private final double leftEps;
	private final double topEps;
	
	
	public PositionManhattanConstraint(int minPts, double leftEps, double topEps) {
		super(minPts);
		this.leftEps = leftEps;
		this.topEps = topEps;
	}

	@Override
	public double[] getEps() {
		return new double[] {this.leftEps, this.topEps};
	}
	
	@Override
	public double distance(ComputableFeature a, ComputableFeature b) {
		PositionFeature pa = (PositionFeature)a;
		PositionFeature pb = (PositionFeature)b;
		
		return Math.abs(pa.getLeft() - pb.getLeft()) + Math.abs(pa.getTop() - pb.getTop());
	}

	@Override
	public boolean isNeighbours(ComputableFeature a, ComputableFeature b) {
		PositionFeature pa = (PositionFeature)a;
		PositionFeature pb = (PositionFeature)b;
		
		if(Math.abs(pa.getLeft() - pb.getLeft()) > this.leftEps
				|| Math.abs(pa.getTop() - pb.getTop()) > this.topEps) {
			return false;
		}

		return true;
	}


}
