package org.hddbscan.dbscan.feature;

import java.util.Objects;


public class PositionFeature implements ComputableFeature {
	private double left;
	private double top;
	private double width;
	private double height;
	
	
	public PositionFeature(double left, double top, double width, double height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	
	public double getLeft() {
		return this.left;
	}
	
	public double getTop() {
		return this.top;
	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	@Override
	public boolean greaterThan(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return this.left > the.left || this.top > the.top;
	}

	@Override
	public boolean lessThan(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return this.left < the.left || this.top < the.top;
	}

	@Override
	public ComputableFeature clone() {
		return new PositionFeature(this.left, this.top, this.width, this.height);
	}
	
	@Override
	public PositionFeature min(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		if(this.left < the.left) {
			the.left = this.left;
		}
		if(this.top < the.top) {
			the.top = this.top;
		}
		
		return the;
	}

	@Override
	public PositionFeature max(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		if(this.left > the.left) {
			the.left = this.left;
		}
		if(this.top > the.top) {
			the.top = this.top;
		}
		
		return the;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.height, this.left, this.top, this.width);
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
		return Double.doubleToLongBits(this.height) == Double.doubleToLongBits(other.height)
				&& Double.doubleToLongBits(this.left) == Double.doubleToLongBits(other.left)
				&& Double.doubleToLongBits(this.top) == Double.doubleToLongBits(other.top)
				&& Double.doubleToLongBits(this.width) == Double.doubleToLongBits(other.width);
	}

	@Override
	public String toString() {
		return "{l=" + this.left + ", t=" + this.top + ", w=" + this.width + ", h=" + this.height + "}";
	}


}
