package org.hddbscan.optics;

import java.util.Arrays;
import java.util.Objects;


public class DataPoint {
	private final double[] coordinates;
	/**
	 * Calculated as (max(text{core-distance}(o), text{distance}(o, p))). It reflects the density threshold required to pull point p into the cluster containing core point o.
	 */
	private double reachabilityDistance = Double.MAX_VALUE; // Equivalent to Undefined
	private double coreDistance = Double.MAX_VALUE; // The minimum radius required to collect at least minPts elements around a data point.
	private boolean processed = false;
	
	
	public DataPoint(double[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public double distanceTo(DataPoint other) {
		double sum = 0;
		for(int i = 0; i < coordinates.length; i++) {
			double diff = this.coordinates[i] - other.coordinates[i];
			sum += diff * diff;
		}
		return Math.sqrt(sum);
	}

	public double getReachabilityDistance() {
		return reachabilityDistance;
	}

	public void setReachabilityDistance(double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}

	public double getCoreDistance() {
		return coreDistance;
	}

	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinates);
		result = prime * result + Objects.hash(coreDistance, processed, reachabilityDistance);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataPoint other = (DataPoint) obj;
		return Arrays.equals(coordinates, other.coordinates)
				&& Double.doubleToLongBits(coreDistance) == Double.doubleToLongBits(other.coreDistance)
				&& processed == other.processed
				&& Double.doubleToLongBits(reachabilityDistance) == Double.doubleToLongBits(other.reachabilityDistance);
	}
	


}
