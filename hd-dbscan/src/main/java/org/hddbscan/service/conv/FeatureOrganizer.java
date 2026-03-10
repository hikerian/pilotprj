package org.hddbscan.service.conv;

import java.util.function.Function;

import org.hddbscan.dbscan.feature.ComputableFeature;
import org.hddbscan.dbscan.feature.DimensionConstraint;
import org.hddbscan.entity.UiElements;


public class FeatureOrganizer<F extends ComputableFeature> {
	private final String label;
	private final DimensionConstraint constraint;
	private final Function<UiElements, F> mapper;
	
	
	public FeatureOrganizer(String label, DimensionConstraint constraint, Function<UiElements, F> mapper) {
		this.label = label;
		this.constraint = constraint;
		this.mapper = mapper;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public ComputableFeature genFeature(UiElements rawdata) {
		return this.mapper.apply(rawdata);
	}
	
	public DimensionConstraint getConstraint() {
		return this.constraint;
	}


}
