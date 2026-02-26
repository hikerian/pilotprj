package org.hddbscan.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.feature.DoubleConstraint;
import org.hddbscan.dbscan.feature.PositionManhattanConstraint;
import org.hddbscan.entity.UiElements;

import net.minidev.json.JSONObject;


public class UIElementDataSet {
	private final String[] labels = {"isButton", "isInput", "isOutput", "position"};
	private final List<UIElementDataRow> dataRowList;
	
	
	public UIElementDataSet() {
		this.dataRowList = new ArrayList<>();
	}
	
	public void addRow(UIElementDataRow dataRow) {
		this.dataRowList.add(dataRow);
	}
	
	public void addRow(UiElements uiElements) {
		UIElementDataRow dataRow = UIElementDataRow.convert(uiElements);
		if(dataRow == null) {
			return;
		}
		
		this.dataRowList.add(dataRow);
	}
	
	public void addRow(String id, JSONObject target) {
		UIElementDataRow dataRow = UIElementDataRow.convert(id, target);
		if(dataRow == null) {
			return;
		}
		
		this.dataRowList.add(dataRow);
	}
	
	public DataSet toDataSet() {
		DataSet dataSet = new DataSet();
		
		dataSet.setLabels(this.labels);
		
		for(UIElementDataRow uiDataRow : this.dataRowList) {
			DataRow dataRow = uiDataRow.toDataRow();
			dataSet.addRow(dataRow);
		}
		
		return dataSet;
	}
	
	public DBSCANMetadata getMetadata() {
		DBSCANMetadata metadata = new DBSCANMetadata();
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in SearchBox
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in DataBox
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in FormBox
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in GridHeader
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in TabfolderHeader

		// "isButton", "isInput", "isOutput", "position"
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isButton
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isInput
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isOutput
		metadata.addConstraint(new PositionManhattanConstraint(1, 200D, 30D));   // position
		
		return metadata;
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append("UIElementDataSet:\n");
		out.append("id").append(delimiter).append(String.join(delimiter, this.labels)).append('\n');
		for(UIElementDataRow dataRow : this.dataRowList) {
			dataRow.print(out, delimiter);
		}
	}
	
	@Override
	public String toString() {
		return "UIElementDataSet [labels=" + Arrays.toString(this.labels) + ",dataRowList=" + this.dataRowList + "]";
	}



}
