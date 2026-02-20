package org.hddbscan.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.feature.DoubleDistance;
import org.hddbscan.dbscan.feature.PositionManhattanDistance;
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
		
		// "isButton", "isInput", "isOutput", "position"
		
		metadata.setMinPts(1);

		metadata.addEps(new DoubleDistance(0.5D));   // isButton
		metadata.addEps(new DoubleDistance(0.5D));   // isInput
		metadata.addEps(new DoubleDistance(0.5D));   // isOutput
		metadata.addEps(new PositionManhattanDistance(200D, 30D));   // position
		
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
