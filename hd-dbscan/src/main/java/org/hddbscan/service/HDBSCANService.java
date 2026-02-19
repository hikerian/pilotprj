package org.hddbscan.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hddbscan.controller.ModelGroup;
import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DBSCANModel;
import org.hddbscan.dbscan.DBSCANModel.DBSCANGroup;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.HDBSCAN;
import org.hddbscan.dbscan.service.UIElementDataRow;
import org.hddbscan.dbscan.service.UIElementDataSet;
import org.hddbscan.entity.UiElements;
import org.hddbscan.repository.DBAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class HDBSCANService {
	private final Logger log = LoggerFactory.getLogger(HDBSCANService.class);
	
	private final DBAccessor dao;
	
	private DBSCANModel model;
	
	
	public HDBSCANService(DBAccessor dbAccessor) {
		this.dao = dbAccessor;
	}
	
	public DBSCANModel learn(List<String> pageIds) {
		
		UIElementDataSet uiDataSet = new UIElementDataSet();
		
		for(String pageId : pageIds) {
			List<UiElements> uiElements = this.dao.selectUiElementsList(Long.parseLong(pageId));
			
			for(UiElements uiElement : uiElements) {
				UIElementDataRow dataRow = UIElementDataRow.convert(uiElement);
				if(dataRow != null) {
					uiDataSet.addRow(dataRow);
				}
			}
			
			this.log.debug("pageId:{} element {} cnt loaded", pageId, uiElements.size());
		}
		
		DataSet dataSet = uiDataSet.toDataSet();
		DBSCANMetadata metadata = uiDataSet.getMetadata();
		
		HDBSCAN hdbscan = new HDBSCAN();
		hdbscan.setMetadata(metadata);
		DBSCANModel model = hdbscan.fit(dataSet);
		
		this.model = model;
		
		return model;
	}
	
	public List<DBSCANGroup> predict(long pageId, long elementId) {
		UiElements uiElement = this.dao.selectUiElements(pageId, elementId);
		UIElementDataRow uiDataRow = UIElementDataRow.convert(uiElement);
		
		try {
			System.out.println("==== UIDataRow ====");
			uiDataRow.print(System.out, ",");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DataRow dataRow = uiDataRow.toDataRow();
		System.out.println("==== DataRow ====");
		System.out.println(dataRow);
		
		List<DBSCANGroup> groupList = this.model.predict(dataRow);
		
		try {
			for(DBSCANGroup group : groupList) {
				System.out.println("==== DBSCANGroup ====");
				group.print(System.out, ",");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return groupList;
	}
	
	public List<ModelGroup> getModelGroups(long pageId) {
		List<ModelGroup> modelGroupList = new ArrayList<>();
		
		StringBuilder builder = new StringBuilder();
		List<UiElements> uiElements = this.dao.selectUiElementsList(pageId);
		
		List<DBSCANGroup> groupList = this.model.getGroups();
		for(DBSCANGroup group : groupList) {
			String id = group.getId();
			try {
				group.printRange(builder, ",");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String rangeTxt = builder.toString();
			builder.delete(0, builder.length());
			
			List<UiElements> filtered = uiElements.stream().filter(
					(element)->group.hasDataRowId(UIElementDataRow.genId(element))
					).toList();
			
			ModelGroup modelGroup = new ModelGroup();
			modelGroup.setId(id);
			modelGroup.setRangeText(rangeTxt);
			modelGroup.setUiElementList(filtered);
			
			modelGroupList.add(modelGroup);
		}
		
		return modelGroupList;
	}



}
