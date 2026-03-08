package org.hddbscan.service.conv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DBSCANCluster;
import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.feature.DoubleConstraint;
import org.hddbscan.dbscan.feature.PositionManhattanConstraint;
import org.hddbscan.entity.UiElements;
import org.hddbscan.preprocessing.RawCluster;

import net.minidev.json.JSONObject;


public class Type1DataSet {
	private final String[] labels = {
			"isInGroupTitle"
			, "isInGridHeader"
			, "isInGridDetail"
			, "isInTabFolderHeader"
			, "isDataTitle"
			, "isButton"
			, "isInput"
			, "isOutput"
			, "position"};

	private final List<RawCluster<Type1DataRow>> dataRowList;
	
	
	public Type1DataSet() {
		this.dataRowList = new ArrayList<>();
	}
	
	public void addCluster(String clusterId, List<UiElements> list) {
		RawCluster<Type1DataRow> cluster = new RawCluster<>(clusterId);
		
		List<Type1DataRow> dataRowList = list.stream().map((element)-> Type1DataRow.convert(element)).toList();
		cluster.addAll(dataRowList);
		
		this.dataRowList.add(cluster);
	}
	
	public void addType1DataRowCluster(String clusterId, List<Type1DataRow> list) {
		RawCluster<Type1DataRow> cluster = new RawCluster<>(clusterId);
		cluster.addAll(list);
		
		this.dataRowList.add(cluster);
	}
	
//	public void addRow(Type1DataRow dataRow) {
//		this.dataRowList.add(dataRow);
//	}
//	
//	public void addRow(UiElements uiElements) {
//		Type1DataRow dataRow = Type1DataRow.convert(uiElements);
//		if(dataRow == null) {
//			return;
//		}
//		
//		this.dataRowList.add(dataRow);
//	}
//	
//	public void addRow(String id, JSONObject target) {
//		Type1DataRow dataRow = Type1DataRow.convert(id, target);
//		if(dataRow == null) {
//			return;
//		}
//		
//		this.dataRowList.add(dataRow);
//	}
	
//	public DataSet toDataSet() {
//		DataSet dataSet = new DataSet();
//		
//		dataSet.setLabels(this.labels);
//		
//		for(Type1DataRow uiDataRow : this.dataRowList) {
//			DataRow dataRow = uiDataRow.toDataRow();
//			dataSet.addRow(dataRow);
//		}
//		
//		return dataSet;
//	}
	
	public DataSet toDataSet() {
		DataSet dataSet = new DataSet();
		
		dataSet.setLabels(this.labels);
		
		for(RawCluster<Type1DataRow> rawCluster : this.dataRowList) {
			List<Type1DataRow> rowList = rawCluster.getList();
			
			DBSCANCluster cluster = new DBSCANCluster(rawCluster.getId(),
					rowList.stream().map((row)->row.toDataRow()).toList());
			
			dataSet.addCluster(cluster);
		}
		
		return dataSet;
	}
	
	public DBSCANMetadata getMetadata() {
//		"isInGroupTitle"
//		, "isInGridHeader"
//		, "isInGridDetail"
//		, "isInTabFolderHeader"
//		, "isDataTitle"
//		, "isButton"
//		, "isInput"
//		, "isOutput"
//		, "position"};		
		
		DBSCANMetadata metadata = new DBSCANMetadata();
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
	
//	public void print(Appendable out, String delimiter) throws IOException {
//		out.append("UIElementDataSet:\n");
//		out.append("id").append(delimiter).append(String.join(delimiter, this.labels)).append('\n');
//		for(RawCluster<Type1DataRow> rawCluster : this.dataRowList) {
//			rawCluster.print(out, delimiter);
//		}
//	}
	
//	@Override
//	public String toString() {
//		return "UIElementDataSet [labels=" + Arrays.toString(this.labels) + ",dataRowList=" + this.dataRowList + "]";
//	}



}
