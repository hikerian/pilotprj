package org.hddbscan.dbscan.feature;


/**
 * TODO: 그냥 클래스로?
 * @param <T>
 */
public interface FeatureOrganizer<T> {
	public String getLabel();
	public ComputableFeature genFeature(T rawdata);
	public DimensionConstraint getConstraint();

}
