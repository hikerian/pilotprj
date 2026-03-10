package org.hddbscan.service.conv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DBSCANCluster;
import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.feature.ComputableFeature;
import org.hddbscan.dbscan.feature.DoubleConstraint;
import org.hddbscan.dbscan.feature.DoubleFeature;
import org.hddbscan.dbscan.feature.PositionFeature;
import org.hddbscan.dbscan.feature.PositionManhattanConstraint;
import org.hddbscan.entity.UiElements;
import org.hddbscan.preprocessing.RawCluster;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


public class DataSetConverter {
	private final List<RawCluster<UiElements>> rawClusterList;
	
	
	public static String genId(UiElements uiElements) {
		return String.format("%1$d-%2$d", uiElements.getPageId(), uiElements.getElementId());
	}
	
	public static UiElements convert(long pageId, long elementId, JSONObject target) {
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		String selector = target.getAsString("selector");
		String[] classNames = classNameArray.toArray(new String[0]);
		String text = target.getAsString("text");
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		double left = clientRect.getAsNumber("left").doubleValue();
		double top = clientRect.getAsNumber("top").doubleValue();
		double width = clientRect.getAsNumber("width").doubleValue();
		double height = clientRect.getAsNumber("height").doubleValue();
		String majorYn = target.getAsString("majorYn");
		
		UiElements element = new UiElements();
		element.setPageId(pageId);
		element.setElementId(elementId);
		element.setSelectorText(selector);
		element.setClassNames(String.join(",", classNames));
		element.setCtntText(text);
		element.setPosLeft(left);
		element.setPosTop(top);
		element.setUiWidth(width);
		element.setUiHeight(height);
		element.setMajorYn(majorYn);
		
		return element;
	}
	
	public static DataRow convert(UiElements element, DataSetConverterMetadata meta) {
		List<FeatureOrganizer<? extends ComputableFeature>> orgList = meta.getFeatureOrganizerList();
		
		return DataSetConverter.convert(element, orgList);
	}
	
	public static DataRow convert(UiElements element, List<FeatureOrganizer<? extends ComputableFeature>> orgList) {
		ComputableFeature[] features = orgList.stream()
				.map((org)-> org.genFeature(element))
				.toArray(ComputableFeature[]::new);

		DataRow dataRow = new DataRow();
		dataRow.setId(DataSetConverter.genId(element));
		dataRow.setData(features);
		
		return dataRow;
	}

	private static boolean isContains(List<String> container, String selector) {
		for(String item : container) {
			if(selector.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isContains(String cond, String[] items) {
		for(String item : items) {
			if(cond.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isContains(List<String> container, String[] items) {
		for(String item : items) {
			if(container.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public DataSetConverter() {
		this.rawClusterList = new ArrayList<>();
	}
	
	public void addCluster(RawCluster<UiElements> cluster) {
		this.rawClusterList.add(cluster);
	}

	public static DataSetConverterMetadata genType1Meta() {
		DataSetConverterMetadata meta = new DataSetConverterMetadata();
		
		final List<String> GROUPTITLE_CLASSES = Arrays.asList("grp-title", "grd-title");
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isInGroupTitle"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String selector = element.getSelectorText();
					return new DoubleFeature(DataSetConverter.isContains(GROUPTITLE_CLASSES, selector) ? 1D : 0D);
				}));
		
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isInGridHeader"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String selector = element.getSelectorText();
					return new DoubleFeature(selector.contains("cl-grid-header") ? 1D : 0D);
				}));
		
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isInGridDetail"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String selector = element.getSelectorText();
					return new DoubleFeature(selector.contains("cl-grid-detail") ? 1D : 0D);
				}));
		
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isInTabFolderHeader"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String selector = element.getSelectorText();
					return new DoubleFeature(selector.contains("cl-tabfolder-header") ? 1D : 0D);
				}));

		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isDataTitle"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String[] classNames = element.getClassNames().split(",");
					
					return new DoubleFeature(DataSetConverter.isContains("data-title", classNames) ? 1D : 0D);
				}));
		
		final List<String> BUTTON_CLASSES = Arrays.asList("btn-header-minus", "cl-button", "btn-search", "btn-restore"
				, "btn-save", "btn-excel", "btn-setting", "btn-pop", "btn-new", "btn-delete", "btn-pop-search");
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isButton"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String[] classNames = element.getClassNames().split(",");
					
					return new DoubleFeature(DataSetConverter.isContains(BUTTON_CLASSES, classNames) ? 1D : 0D);
				}));
		
		final List<String> INPUT_CLASSES = Arrays.asList("cl-inputbox", "cl-combobox", "cl-numbereditor", "cl-checkbox", "cl-dateinput");
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isInput"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String[] classNames = element.getClassNames().split(",");
					
					return new DoubleFeature(DataSetConverter.isContains(INPUT_CLASSES, classNames) ? 1D : 0D);
				}));

		final List<String> OUTPUT_CLASSES = Arrays.asList("cl-output", "cl-default-cell", "table-row-cnt");
		meta.addFeatureOrganizer(new FeatureOrganizer<DoubleFeature>("isOutput"
				, new DoubleConstraint(1, 0.5D)
				, (UiElements element)-> {
					String[] classNames = element.getClassNames().split(",");
					
					return new DoubleFeature(DataSetConverter.isContains(OUTPUT_CLASSES, classNames) ? 1D : 0D);
				}));

		meta.addFeatureOrganizer(new FeatureOrganizer<PositionFeature>("position"
				, new PositionManhattanConstraint(1, 200D, 10D)
				, (UiElements element)-> {
					double left = element.getPosLeft();
					double top = element.getPosTop();
					double width = element.getUiWidth();
					double height = element.getUiHeight();
					
					return new PositionFeature(left, top, width, height);
				}));
		
		return meta;
	}
	
	public DataSet genDataSet(DataSetConverterMetadata meta) {
		DataSet dataSet = new DataSet();
		
		List<FeatureOrganizer<? extends ComputableFeature>> orgList = meta.getFeatureOrganizerList();
		String[] labels = orgList.stream()
				.map((org)-> org.getLabel())
				.toArray(String[]::new);
		dataSet.setLabels(labels);
		
		for(RawCluster<UiElements> rawCluster : this.rawClusterList) {
			List<DataRow> dataRowList = new ArrayList<>();
			List<UiElements> uiElementsList = rawCluster.getList();
			for(UiElements element : uiElementsList) {
				DataRow dataRow = DataSetConverter.convert(element, orgList);
				dataRowList.add(dataRow);
			}
			
			dataSet.addCluster(new DBSCANCluster(rawCluster.getId(), dataRowList));
		}
		
		return dataSet;
	}
	
	public DBSCANMetadata genDBSCANMetadata(DataSetConverterMetadata meta) {
		final DBSCANMetadata metadata = new DBSCANMetadata();
		
		List<FeatureOrganizer<? extends ComputableFeature>> orgList = meta.getFeatureOrganizerList();
		orgList.forEach((FeatureOrganizer<? extends ComputableFeature> org) -> {
			metadata.addConstraint(org.getConstraint());
		});
		
		return metadata;
	}




}
