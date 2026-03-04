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
	public boolean isNeighbours(ComputableFeature a, ComputableFeature b) {
		PositionFeature pa = (PositionFeature)a;
		PositionFeature pb = (PositionFeature)b;
		
		double xd = 0D;
		double yd = 0D;
		
		// 너비 포함 계산
		if(pa.getLeft() == pb.getLeft()) {
			xd = 0D;
		} else {
			xd = Math.abs(pa.getLeft() - pb.getLeft());
			
			if(pa.getLeft() < pb.getLeft()) {
				xd = Math.min(Math.abs(pa.getLeft() + pa.getWidth() - pb.getLeft()), xd) ;
			} else {
				xd = Math.min(Math.abs(pa.getLeft() - (pb.getLeft() + pb.getWidth())), xd);
			}
		}
		
		// 높이 포함 계산
		if(pa.getTop() == pb.getTop()) {
			yd = 0D;
		} else {
			yd = Math.abs(pa.getTop() - pb.getTop());
			
			if(pa.getTop() < pb.getTop()) {
				yd = Math.min(Math.abs(pa.getTop() + pa.getHeight() - pb.getTop()), yd);
			} else {
				yd = Math.min(Math.abs(pa.getTop() - (pb.getTop() + pb.getHeight())), yd);
			}
		}
		
		if(xd > this.leftEps || yd > this.topEps) {
			return false;
		}
		
		return true;
	}


}
