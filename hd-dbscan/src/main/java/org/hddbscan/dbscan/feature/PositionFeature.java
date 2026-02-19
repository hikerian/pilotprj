package org.hddbscan.dbscan.feature;

import java.util.Objects;


public class PositionFeature implements ComputableFeature {
	private double left;
	private double top;
	
	
	public PositionFeature(double left, double top) {
		this.left = left;
		this.top = top;
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
	public double distance(ComputableFeature other) {
		PositionFeature the = (PositionFeature)other;
		return Math.sqrt(Math.pow(this.left - the.left, 2)
				+ Math.pow(this.top - the.top, 2));
	}

	@Override
	public ComputableFeature clone() {
		return new PositionFeature(this.left, this.top);
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
		return Objects.hash(this.left, this.top);
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
		return Double.doubleToLongBits(this.left) == Double.doubleToLongBits(other.left)
				&& Double.doubleToLongBits(this.top) == Double.doubleToLongBits(other.top);
	}

	@Override
	public String toString() {
		return "Position [l=" + this.left + ", t=" + this.top + "]";
	}


}
