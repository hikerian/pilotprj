package org.hddbscan.dbscan.backup;

import java.util.Objects;


public class DBSCANRange {
	private Double min;
	private Double max;
	
	
	public DBSCANRange() {
		
	}
	
	public DBSCANRange(Double min, Double max) {
		this.min = min;
		this.max = max;
	}

	public Double getMin() {
		return this.min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return this.max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

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
