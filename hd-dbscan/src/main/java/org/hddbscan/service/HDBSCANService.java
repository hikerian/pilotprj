package org.hddbscan.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hddbscan.controller.ModelGroup;
import org.hddbscan.dbscan.DBSCANMetadata;
import org.hddbscan.dbscan.DBSCANModel;
import org.hddbscan.dbscan.DBSCANModel.DBSCANGroup;
import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.DataSet;
import org.hddbscan.dbscan.HDBSCAN;
import org.hddbscan.dbscan.model.DBSCANGroupModel;
import org.hddbscan.entity.ClusterModel;
import org.hddbscan.entity.UiElements;
import org.hddbscan.preprocessing.RawCluster;
import org.hddbscan.repository.DBAccessor;
import org.hddbscan.service.conv.DataSetConverter;
import org.hddbscan.service.conv.DataSetConverterMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;


@Service
public class HDBSCANService {

	private final Logger log = LoggerFactory.getLogger(HDBSCANService.class);
	
//	private final String clusterIdExeptClass = "\\.cl-first-row|\\.cl-first-column|\\.cl-last-row|\\.cl-last-column|\\.cl-odd-column|\\.cl-odd-row|\\.cl-even-column|\\.cl-even-row|:nth-of-type\\(\\d+\\)";
	
	private final DBAccessor dao;
	
	private DBSCANModel model;
	
	
	public HDBSCANService(DBAccessor dbAccessor) {
		this.dao = dbAccessor;
	}
	
	public DBSCANModel learn(List<String> pageIds) {
		long start = System.currentTimeMillis();
		
		List<UiElements> elementList = new ArrayList<>();
		
		for(String pageId : pageIds) {
			List<UiElements> uiElements = this.dao.selectUiElementsList(Long.parseLong(pageId));
			this.log.debug("pageId:{} element {} cnt loaded", pageId, uiElements.size());
			
			elementList.addAll(uiElements);
		}
		
		// sort
		elementList.sort((UiElements el1, UiElements el2)-> {
			return el1.getSelectorText().compareTo(el2.getSelectorText());
		});
		
		// tree의 그룹요소를 1차원 cluster로 펼치기
		final String OTHER = "OTHER";
		Map<String, RawCluster<UiElements>> clusterMap = new HashMap<>();
		RawCluster<UiElements> other = new RawCluster<>(OTHER);
		clusterMap.put(OTHER, other);
		
		final String groupClassNames = "search-box|data-box|form-box";
		Pattern pattern = Pattern.compile(groupClassNames);
		List<String> clusterIdClass = new ArrayList<>();
		
		int maxCnt = 0;
		elementLoop: for(UiElements raw : elementList) {
			maxCnt++;
			clusterIdClass.clear();
			String selector = raw.getSelectorText();
			
			Matcher matcher = pattern.matcher(selector);
			while(matcher.find()) {
				clusterIdClass.add(matcher.group());
			}
			if(clusterIdClass.size() > 0) {
				String clusterId = String.join(" > ", clusterIdClass.toArray(new String[0]));				
				RawCluster<UiElements> cluster = clusterMap.get(clusterId);
				if(cluster == null) {
					cluster = new RawCluster<>(clusterId);
					clusterMap.put(clusterId, cluster);
					
					this.log.debug("New Preprocessing ClusterID: {}", clusterId);
				}
				cluster.add(raw);
				
				continue elementLoop;
			} else {
				other.add(raw);
			}
		}
		
		this.log.info("Preprocessing Cluster count: {}, Raw count: {}, time: {} ms spent"
				, clusterMap.size(), maxCnt, System.currentTimeMillis() - start);
		
		DataSetConverter converter = new DataSetConverter();
		clusterMap.values().forEach((cluster) -> {
			try {
				cluster.print(System.out, ",");
			} catch (IOException e) { }
			
			converter.addCluster(cluster);
		});
		
		DataSetConverterMetadata meta = this.getConverterMeta();
		DataSet dataSet = converter.genDataSet(meta);
		DBSCANMetadata metadata = converter.genDBSCANMetadata(meta);
		
		HDBSCAN hdbscan = new HDBSCAN();
		hdbscan.setMetadata(metadata);
		DBSCANModel model = hdbscan.fit(dataSet);
		
		this.model = model;
		
		// serialize & deserialize
//		{
//			DBSCANGroupModel serializableModel = new DBSCANGroupModel(model);
//			JsonMapper.Builder builder = JsonMapper.builder();
//			JsonMapper mapper = builder.build();
//			
//			StringWriter out = new StringWriter();
//			mapper.writer().writeValue(out, serializableModel);
//			
//			String json = out.toString();
//			
//			System.out.println("====================================================");
//			System.out.println(json);
//			System.out.println("====================================================");
//			
//			DBSCANGroupModel deserializedModel = mapper.readValue(json, DBSCANGroupModel.class);
//			
//			System.out.println(deserializedModel);
//			System.out.println("====================================================");
//		}
		
		return model;
	}
	
	public boolean saveModel(String modelDesc) {
		if(this.model == null) {
			return false;
		}
		
		DBSCANGroupModel serializableModel = new DBSCANGroupModel(this.model);
		JsonMapper.Builder builder = JsonMapper.builder();
		JsonMapper mapper = builder.build();
		
		StringWriter out = new StringWriter();
		mapper.writer().writeValue(out, serializableModel);
		
		String json = out.toString();
		
		ClusterModel clusterModel = new ClusterModel(System.currentTimeMillis()
				, modelDesc, json);
		
		this.dao.insertClusterModel(clusterModel);
		
		return true;
	}
	
	public boolean loadModel(long modelId) {
		ClusterModel clusterModel = this.dao.selectClusterModel(modelId);
		if(clusterModel == null) {
			return false;
		}
		
		String json = clusterModel.getModelJson();
		
		JsonMapper.Builder builder = JsonMapper.builder();
		JsonMapper mapper = builder.build();
		
		DBSCANGroupModel deserializedModel = mapper.readValue(json, DBSCANGroupModel.class);
		
		DBSCANModel model = new DBSCANModel(deserializedModel);
		
		this.model = model;
		
		return true;
	}
	
	public List<ClusterModel> selectClusterModelList() {
		return this.dao.selectClusterModelList();
	}
	
	public int deleteModel(long modelId) {
		return this.dao.deleteClusterModel(modelId);
	}
	
//	private String genClusterId(String[] splitedSelector, int maxIdx) {
//		String[] newSelector = new String[maxIdx + 1];
//		System.arraycopy(splitedSelector, 0, newSelector, 0, maxIdx + 1);
//		
//		String parentPath = String.join(" > ", newSelector);
//		parentPath = parentPath.replaceAll(this.clusterIdExeptClass, "");
//		
//		return parentPath;
//	}
	
//	public static void main(String[] args) {
////		String selector = "body > div.cl-control.cl-container > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-first-column.cl-first-row.cl-last-row.cl-odd-column.cl-odd-row.data-box:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-first-column.cl-first-row.cl-last-column.cl-odd-column.cl-odd-row:nth-of-type(2) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-embeddedapp.cl-first-column.cl-first-row.cl-last-row.cl-odd-column.cl-odd-row:nth-of-type(1) > div.cl-control.cl-container.grd-title > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-control.cl-container > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-output.cl-even-column.cl-first-row.cl-last-row.cl-odd-row.cl-selectable.cl-writingmode-horizontal-tb.table-row-cnt:nth-of-type(2)";
//		String[] selectors = {
//				"body > div.cl-control.cl-container > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-column.cl-first-row.cl-last-column.cl-last-row.cl-odd-row.data-box:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-first-column.cl-first-row.cl-last-column.cl-last-row.cl-odd-column.cl-odd-row:nth-of-type(1) > div > div.cl-tabfolder-body:nth-of-type(2) > div > div.cl-control.cl-container > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column.form-box:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-output.cl-first-column.cl-first-row.cl-odd-column.cl-odd-row.cl-selectable.cl-writingmode-horizontal-tb.require:nth-of-type(3)" // [data-box, form-box]
//				, "body > div.cl-control.cl-container > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-column.cl-first-row.cl-last-column.cl-last-row.cl-odd-row.data-box:nth-of-type(2) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(2) > div > div.cl-tabfolder-body:nth-of-type(2) > div > div.cl-control.cl-container > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column.form-box:nth-of-type(2) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-output.cl-first-column.cl-first-row.cl-odd-column.cl-odd-row.cl-selectable.cl-writingmode-horizontal-tb.require:nth-of-type(1)" // [data-box, form-box]
//				, "body > div.cl-control.cl-container > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-last-row.cl-layout-wrap:nth-of-type(4) > div.cl-control.cl-container.cl-last-row > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-even-column.cl-first-row.cl-last-column.cl-last-row.cl-odd-row.data-box:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-even-row.cl-first-column.cl-last-column.cl-last-row.cl-odd-column:nth-of-type(2) > div > div.cl-tabfolder-body:nth-of-type(2) > div > div.cl-control.cl-container.data-box > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.cl-control.cl-container.cl-first-column.cl-first-row.cl-last-column.cl-odd-column.cl-odd-row:nth-of-type(1) > div.cl-layout.cl-scrollbar > div.cl-layout-content > div.btn-save.cl-control.cl-button.cl-first-row.cl-last-column.cl-last-row.cl-odd-column.cl-odd-row.cl-unselectable:nth-of-type(5)"  // [data-box, data-box] 오류!!
//		};
//		
//		final String groupClassNames = "search-box|data-box|form-box";
//		Pattern pattern = Pattern.compile(groupClassNames);
//		
//		for(String selector : selectors) {
//			Matcher matcher = pattern.matcher(selector);
//			List<String> clusterId = new ArrayList<>();
//			while(matcher.find()) {
//				clusterId.add(matcher.group());
//			}
//			System.out.println(clusterId);
//		}
//		
//		
////		String[] removal = {
////				"\\.cl-first-row"
////				, "\\.cl-first-column"
////				, "\\.cl-last-row"
////				, "\\.cl-last-column"
////				, "\\.cl-odd-column"
////				, "\\.cl-odd-row"
////				, "\\.cl-even-column"
////				, "\\.cl-even-row"
////				, ":nth-of-type\\(\\d+\\)"
////		};
////		
////		String remove = "\\.cl-first-row|\\.cl-first-column|\\.cl-last-row|\\.cl-last-column|\\.cl-odd-column|\\.cl-odd-row|\\.cl-even-column|\\.cl-even-row|:nth-of-type\\(\\d+\\)";
////		
//////		for(String re : removal) {
//////			selector = selector.replaceAll(re, "");
//////		}
////		for(String selector : selectors) {
////			selector = selector.replaceAll(remove, "");
////			System.out.println(selector);			
////		}
//		
//	}
	
	public List<DBSCANGroup> predict(long pageId, long elementId) {
		UiElements uiElement = this.dao.selectUiElements(pageId, elementId);
		return this.predict(uiElement);
	}
	public List<DBSCANGroup> predict(UiElements uiElement) {
		DataSetConverterMetadata meta = this.getConverterMeta();
		DataRow dataRow = DataSetConverter.convert(uiElement, meta);
		return this.predict(dataRow);
	}
	public List<DBSCANGroup> predict(DataRow dataRow) {		
		List<DBSCANGroup> groupList = this.model.predict(dataRow);		
		return groupList;
	}
	
	public void setGroupLabel(String groupId, String groupLabel) {
		this.model.setGroupLabel(groupId, groupLabel);
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
					(element)->group.hasDataRowId(DataSetConverter.genId(element))
					).toList();
			
			ModelGroup modelGroup = new ModelGroup();
			modelGroup.setId(id);
			modelGroup.setLabel(group.getLabel());
			modelGroup.setRangeText(rangeTxt);
			modelGroup.setUiElementList(filtered);
			
			modelGroupList.add(modelGroup);
		}
		
		return modelGroupList;
	}

	private DataSetConverterMetadata getConverterMeta() {
		DataSetConverterMetadata meta = DataSetConverter.genType1Meta();
		return meta;
	}
	
	


}
