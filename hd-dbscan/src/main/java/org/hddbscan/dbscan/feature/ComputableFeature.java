package org.hddbscan.dbscan.feature;

public interface ComputableFeature {
	/**
	 * other 보다 크거나 같으면 true 아니면 false.
	 * @param other
	 * @return
	 */
	public boolean greaterThanOrEqualTo(ComputableFeature other);
	/**
	 * other 보다 작거나 같으면 true 아니면 false.
	 * @param other
	 * @return
	 */
	public boolean lessThanOrEqualTo(ComputableFeature other);
	public double distance(ComputableFeature other);

}
