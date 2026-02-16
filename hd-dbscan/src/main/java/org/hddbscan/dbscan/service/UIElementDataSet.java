package org.hddbscan.dbscan.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


public class UIElementDataSet {
	private final List<String> BUTTON_CLASSES = Arrays.asList("btn-header-minus", "cl-button", "btn-search", "btn-restore", "btn-save", "btn-excel"
			, "btn-setting", "btn-pop", "btn-new", "btn-delete", "btn-pop-search");
	private final List<String> INPUT_CLASSES = Arrays.asList("cl-inputbox", "cl-combobox", "cl-numbereditor", "cl-checkbox", "cl-dateinput");
	private final List<String> OUTPUT_CLASSES = Arrays.asList("cl-output", "data-title", "table-row-cnt");
	
	private final List<UIElementDataRow> dataRowList;
	private final String[] labels = {"isButton", "isInput", "isOutput", "left", "top"};
//	private final String[] attrLabels = {"width", "height", "selector", "text", "classNames", "major"};
	
	
	public UIElementDataSet() {
		this.dataRowList = new ArrayList<>();
	}
	
	public void addRow(String id, JSONObject target) {
		String selector = target.getAsString("selector");
		boolean major = (boolean) target.get("major");
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		String[] classNms = classNameArray.toArray(new String[0]);
		String text = target.getAsString("text");
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		double left = clientRect.getAsNumber("left").doubleValue();
		double top = clientRect.getAsNumber("top").doubleValue();
		double width = clientRect.getAsNumber("width").doubleValue();
		double height = clientRect.getAsNumber("height").doubleValue();
		
		UIElementDataRow dataRow = new UIElementDataRow();
		dataRow.setId(id);
		dataRow.setButtonEl(this.isContains(this.BUTTON_CLASSES, classNms));
		dataRow.setInputEl(this.isContains(this.INPUT_CLASSES, classNms));
		dataRow.setOutputEl(this.isContains(this.OUTPUT_CLASSES, classNms));
		dataRow.setLeft(left);
		dataRow.setTop(top);
//		dataRow.setWidth(width);
//		dataRow.setHeight(height);
//		dataRow.setSelector(selector);
//		dataRow.setText(text);
//		dataRow.setClassNames(classNms);
//		dataRow.setMajor(major);
		
		this.dataRowList.add(dataRow);
	}
	
	private boolean isContains(List<String> container, String[] items) {
		for(String item : items) {
			if(container.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	public DataSet toDataSet() {
		DataSet dataSet = new DataSet();
		
		dataSet.setLabels(this.labels);
//		dataSet.setAttrLabels(this.attrLabels);
		
		for(UIElementDataRow uiDataRow : this.dataRowList) {
			DataRow dataRow = uiDataRow.toDataRow();
			dataSet.addRow(dataRow);
		}
		
		return dataSet;
	}
	
	public DBSCANMetadata getMetadata() {
		DBSCANMetadata metadata = new DBSCANMetadata();
		
		// "isButton", "isInput", "isOutput", "left", "top"
		
		metadata.setMinPts(1);
		metadata.addEps(0.5D);   // isButton
		metadata.addEps(0.5D);   // isInput
		metadata.addEps(0.5D);   // isOutput
		metadata.addEps(100D);   // left
		metadata.addEps(50D);    // top
		
		return metadata;
	}
	
	public void print(Appendable out) throws IOException {
		out.append("UIElementDataSet:\n");
		out.append("id,").append(String.join(",", this.labels)).append('\n');
		for(UIElementDataRow dataRow : this.dataRowList) {
			dataRow.print(out);
		}
	}
	
	@Override
	public String toString() {
//		return "UIElementDataSet [dataRowList=" + dataRowList + ", labels=" + Arrays.toString(labels) + ", attrLabels="
//				+ Arrays.toString(attrLabels) + "]";
		return "UIElementDataSet [dataRowList=" + dataRowList + ", labels=" + Arrays.toString(labels) + "]";
	}




}
