package org.hddbscan.dbscan.feature;

import java.util.Objects;


public class DoubleFeature implements ComputableFeature {
	private double value;
	
	
	public DoubleFeature(double value) {
		this.value = value;
	}
	
	@Override
	public boolean greaterThanOrEqualTo(ComputableFeature other) {
		DoubleFeature the = (DoubleFeature)other;
		return this.value >= the.value;
	}

	@Override
	public boolean lessThanOrEqualTo(ComputableFeature other) {
		DoubleFeature the = (DoubleFeature)other;
		return this.value <= the.value;
	}

	@Override
	public double distance(ComputableFeature other) {
		DoubleFeature the = (DoubleFeature)other;
		return Math.abs(this.value - the.value);
	}
	
	@Override
	public ComputableFeature min(ComputableFeature other) {
		DoubleFeature the = (DoubleFeature)other;
		
		if(this.value < the.value) {
			the.value = this.value;
		}

		return the;
	}

	@Override
	public DoubleFeature max(ComputableFeature other) {
		DoubleFeature the = (DoubleFeature)other;
		
		if(this.value > the.value) {
			the.value = this.value;
		}

		return the;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleFeature other = (DoubleFeature) obj;
		return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
	}

	@Override
	public String toString() {
		return "DoubleFeature [value=" + this.value + "]";
	}


}
