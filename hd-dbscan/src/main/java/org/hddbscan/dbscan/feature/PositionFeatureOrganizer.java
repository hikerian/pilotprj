package org.hddbscan.dbscan.feature;

import java.util.function.Function;


public class PositionFeatureOrganizer<T> implements FeatureOrganizer<T> {
	private final String label;
	private final PositionManhattanConstraint constraint;
	private final Function<T, PositionFeature> mapper;
	
	
	public PositionFeatureOrganizer(String label, PositionManhattanConstraint constraint, Function<T, PositionFeature> mapper) {
		this.label = label;
		this.constraint = constraint;
		this.mapper = mapper;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public ComputableFeature genFeature(T rawdata) {
		PositionFeature feature = this.mapper.apply(rawdata);
		return feature;
	}

	@Override
	public DimensionConstraint getConstraint() {
		return this.constraint;
	}

}
