package org.hddbscan.service.conv;

import java.util.ArrayList;
import java.util.List;

import org.hddbscan.dbscan.feature.ComputableFeature;


public class DataSetConverterMetadata {
	private final List<FeatureOrganizer<? extends ComputableFeature>> organizerList;
	
	
	public DataSetConverterMetadata() {
		organizerList = new ArrayList<>();
	}
	
	public void addFeatureOrganizer(FeatureOrganizer<? extends ComputableFeature> organizer) {
		this.organizerList.add(organizer);
	}
	
	public List<FeatureOrganizer<? extends ComputableFeature>> getFeatureOrganizerList() {
		return this.organizerList;
	}


}
