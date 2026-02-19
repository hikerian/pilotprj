package org.hddbscan.dbscan.feature;


public class PositionManhattanDistance implements Distance {
	private final double leftEps;
	private final double topEps;
	
	
	public PositionManhattanDistance(double leftEps, double topEps) {
		this.leftEps = leftEps;
		this.topEps = topEps;
	}

	@Override
	public double distance(ComputableFeature a, ComputableFeature b) {
		PositionFeature pa = (PositionFeature)a;
		PositionFeature pb = (PositionFeature)b;
		
		return Math.abs(pa.getLeft() - pb.getLeft()) + Math.abs(pa.getTop() - pb.getTop());
	}

	@Override
	public double[] getEps() {
		return new double[] {this.leftEps, this.topEps};
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
