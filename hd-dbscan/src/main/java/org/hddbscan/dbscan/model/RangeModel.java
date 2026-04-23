package org.hddbscan.dbscan.model;

import java.util.Map;
import java.util.Objects;

import org.hddbscan.dbscan.DBSCANRange;


public class RangeModel {
	private Map<String, Object> min;
	private Map<String, Object> max;
	
	
	public RangeModel() {
		
	}
	
	public RangeModel(DBSCANRange range) {
		this.min = range.getMin().toMap();
		this.max = range.getMax().toMap();
	}

	public Map<String, Object> getMin() {
		return min;
	}

	public void setMin(Map<String, Object> min) {
		this.min = min;
	}

	public Map<String, Object> getMax() {
		return max;
	}

	public void setMax(Map<String, Object> max) {
		this.max = max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(max, min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RangeModel other = (RangeModel) obj;
		return Objects.equals(max, other.max) && Objects.equals(min, other.min);
	}
	


}
