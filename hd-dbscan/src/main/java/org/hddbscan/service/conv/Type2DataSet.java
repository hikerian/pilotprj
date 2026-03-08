package org.hddbscan.service.conv;

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


public class Type2DataSet {
	private final String[] labels = {"isInSearchBor", "isInDataBox", "isInFormBox", "isInGroupTitle", "isInGridHeader", "isInGridDetail", "isInTabFolderHeader"
			, "isDataTitle", "isButton", "isInput", "isOutput", "position"};
	private final List<Type1DataRow> dataRowList;
	
	
	public Type2DataSet() {
		this.dataRowList = new ArrayList<>();
	}
	
	public void addRow(Type1DataRow dataRow) {
		this.dataRowList.add(dataRow);
	}
	
	public void addRow(UiElements uiElements) {
		Type1DataRow dataRow = Type1DataRow.convert(uiElements);
		if(dataRow == null) {
			return;
		}
		
		this.dataRowList.add(dataRow);
	}
	
	public void addRow(String id, JSONObject target) {
		Type1DataRow dataRow = Type1DataRow.convert(id, target);
		if(dataRow == null) {
			return;
		}
		
		this.dataRowList.add(dataRow);
	}
	
	public DataSet toDataSet() {
		DataSet dataSet = new DataSet();
		
		dataSet.setLabels(this.labels);
		
		for(Type1DataRow uiDataRow : this.dataRowList) {
			DataRow dataRow = uiDataRow.toDataRow();
//			dataSet.addRow(dataRow); // FIXME: preprocessing 지원
		}
		
		return dataSet;
	}
	
	public DBSCANMetadata getMetadata() {
		DBSCANMetadata metadata = new DBSCANMetadata();
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in SearchBox
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in DataBox
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in FormBox
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in GroupTitle
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in GridHeader
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in GridDetail
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // is in TabfolderHeader
		
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isDataTitle

		// "isButton", "isInput", "isOutput", "position"
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isButton
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isInput
		metadata.addConstraint(new DoubleConstraint(1, 0.5D));   // isOutput
//		metadata.addConstraint(new PositionManhattanConstraint(1, 200D, 30D));   // position
		metadata.addConstraint(new PositionManhattanConstraint(1, 200D, 10D));   // position
		
		return metadata;
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append("UIElementDataSet:\n");
		out.append("id").append(delimiter).append(String.join(delimiter, this.labels)).append('\n');
		for(Type1DataRow dataRow : this.dataRowList) {
			dataRow.print(out, delimiter);
		}
	}
	
	@Override
	public String toString() {
		return "UIElementDataSet [labels=" + Arrays.toString(this.labels) + ",dataRowList=" + this.dataRowList + "]";
	}



}
