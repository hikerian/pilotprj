package org.hddbscan.dbscan.feature;

import java.util.function.Function;


public class DoubleFeatureOrganizer<T> implements FeatureOrganizer<T> {
	private final String label;
	private final DoubleConstraint constraint;
	private final Function<T, Double> mapper;
	
	
	public DoubleFeatureOrganizer(String label, int minPts, double eps, Function<T, Double> mapper) {
		this.label = label;
		this.constraint = new DoubleConstraint(minPts, eps);
		this.mapper = mapper;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public ComputableFeature genFeature(T rawdata) {
		Double value = this.mapper.apply(rawdata);

		return new DoubleFeature(value);
	}

	@Override
	public DimensionConstraint getConstraint() {
		return this.constraint;
	}

}
