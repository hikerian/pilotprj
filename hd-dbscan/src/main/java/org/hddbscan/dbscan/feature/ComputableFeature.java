package org.hddbscan.dbscan.feature;

import java.util.Map;


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

	/**
	 * 직렬화 지원
	 * @return
	 */
	public Map<String, Object> toMap();
	
	/**
	 * 직렬화 복원
	 * @param rawData
	 * @return
	 */
	public static ComputableFeature fromMap(Map<String, Object> rawData) {
		ComputableFeature feature = null;
		
		feature = DoubleFeature.fromMap(rawData);
		if(feature == null) {
			feature = PositionFeature.fromMap(rawData);
		}
		
		return feature;
	}


}
