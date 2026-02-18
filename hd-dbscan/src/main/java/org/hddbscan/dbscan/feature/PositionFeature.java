package org.hddbscan.dbscan.feature;

import java.util.Objects;


public class PositionFeature implements ComputableFeature {
	private final double left;
	private final double top;
	private final double width;
	private final double height;
	
	
	public PositionFeature(double left, double top, double width, double height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean greaterThanOrEqualTo(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return this.left + this.width >= the.left + the.width
				&& this.top + this.height >= the.top + the.height;
	}

	@Override
	public boolean lessThanOrEqualTo(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return this.left <= the.left && this.top <= the.top;
	}

	@Override
	public double distance(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return Math.sqrt(Math.pow(this.left - the.left, 2)
				+ Math.pow(this.top - the.top, 2));
	}

	@Override
	public int hashCode() {
		return Objects.hash(height, left, top, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PositionFeature other = (PositionFeature) obj;
		return Double.doubleToLongBits(height) == Double.doubleToLongBits(other.height)
				&& Double.doubleToLongBits(left) == Double.doubleToLongBits(other.left)
				&& Double.doubleToLongBits(top) == Double.doubleToLongBits(other.top)
				&& Double.doubleToLongBits(width) == Double.doubleToLongBits(other.width);
	}

	@Override
	public String toString() {
		return "PositionFeature [left=" + left + ", top=" + top + ", width=" + width + ", height=" + height + "]";
	}


}
