package org.hddbscan.dbscan;

import java.util.Objects;

import org.hddbscan.dbscan.feature.ComputableFeature;


public class DBSCANRange {
	private ComputableFeature min;
	private ComputableFeature max;
	
	
	public DBSCANRange() {
		
	}
	
	public DBSCANRange(ComputableFeature min, ComputableFeature max) {
		this.min = min;
		this.max = max;
	}

	public ComputableFeature getMin() {
		return this.min;
	}

//	public void setMin(ComputableFeature min) {
//		this.min = min;
//	}

	public ComputableFeature getMax() {
		return this.max;
	}

//	public void setMax(ComputableFeature max) {
//		this.max = max;
//	}

	@Override
	public int hashCode() {
		return Objects.hash(this.max, this.min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBSCANRange other = (DBSCANRange) obj;
		return Objects.equals(this.max, other.max) && Objects.equals(this.min, other.min);
	}

	@Override
	public String toString() {
		return "HDSCRange [min=" + this.min + ", max=" + this.max + "]";
	}



}
