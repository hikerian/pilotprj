package org.hddbscan.dbscan.feature;


public interface ComputableFeature extends Cloneable {
	/**
	 * other 보다 크면 true 아니면 false.
	 * @param other
	 * @return
	 */
	public boolean greaterThan(ComputableFeature other);

	/**
	 * other 보다 작으면 true 아니면 false.
	 * @param other
	 * @return
	 */
	public boolean lessThan(ComputableFeature other);
	
	public ComputableFeature clone();
	public ComputableFeature min(ComputableFeature other);
	public ComputableFeature max(ComputableFeature other);

}
