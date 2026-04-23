package org.hddbscan.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hddbscan.dbscan.DBSCANModel;
import org.hddbscan.dbscan.DBSCANModel.DBSCANGroup;
import org.hddbscan.entity.UiElements;
import org.hddbscan.entity.UiPage;
import org.hddbscan.repository.DBAccessor;
import org.hddbscan.service.ExtService;
import org.hddbscan.service.HDBSCANService;
import org.hddbscan.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/rest")
public class ExtController {
	private final Logger log = LoggerFactory.getLogger(ExtController.class);
	
	private final ExtService extService;
	private final StoreService storeService;
	private final HDBSCANService hdbscanService;
	
	private final DBAccessor dao;
	
	
	public ExtController(ExtService extService, StoreService storeService, HDBSCANService hdbscanService, DBAccessor dbAccessor) {
		this.extService = extService;
		this.storeService = storeService;
		this.hdbscanService = hdbscanService;
		
		this.dao = dbAccessor;
	}
	
	@PostMapping("/page-components")
	public ResponseData pageComponents(@RequestBody JSONObject data) {
		this.log.debug("received data: {}", data);
		
		String filePath = this.extService.savePageComponentFile(data);
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("filePath", filePath);
		
		
		return res;
	}
	
	/**
	 * 파일에 저장된 컴포넌트 정보를 DB에 저장
	 * @param data
	 * @return
	 */
	@PostMapping("/ui-compfiles/store")
	public ResponseData storeUiCompfiles(@RequestBody JSONObject data) {
		this.log.debug("received data: {}", data);
		
		String baseDir = data.getAsString("baseDir");
		
		this.storeService.register(baseDir);
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("baseDir", baseDir);
		
		return res;
	}
	
	/**
	 * DB에 저장된 페이지 목록 조회
	 * @return
	 */
	@GetMapping("/ui-pages")
	public ResponseData loadUiPages() {
		List<UiPage> uiPageList = this.dao.selectUiPageList();
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("uiPageList", uiPageList);
		
		return res;
	}
	
	/**
	 * 선택된 페이지의 요소를 이용하여 학습시킴
	 * @param data
	 * @return
	 */
	@PostMapping("/learn-pages")
	public ResponseData learnPages(@RequestBody JSONObject data) {
		@SuppressWarnings("unchecked")
		List<String> pageIds = (List<String>)data.get("pageIds");
		
		this.log.debug("selected pageIds: {}", pageIds);
		
		DBSCANModel model = this.hdbscanService.learn(pageIds);
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("model", model);
		
		return res;
	}
	
	@GetMapping("/page-comps/{pageId}")
	public ResponseData getPageComps(@PathVariable String pageId) {
		List<UiElements> uiElements = this.dao.selectUiElementsList(Long.parseLong(pageId));
		
		// 필터
//		uiElements = uiElements.stream().filter(element -> "Y".equals(element.getMajorYn())).toList();
		
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("uiElements", uiElements);
		
		return res;
	}
	
	@GetMapping("/predict/{pageId}/{elementId}")
	public ResponseData predict(@PathVariable("pageId") String pageId, @PathVariable("elementId") String elementId) {
		
		List<DBSCANGroup> groupList = this.hdbscanService.predict(Long.parseLong(pageId), Long.parseLong(elementId));
		
		StringBuilder result = new StringBuilder();
		if(groupList != null && groupList.size() > 0) {
			for(DBSCANGroup group : groupList) {
				result
					.append("Group(")
					.append(group.getId())
					.append(") {")
					.append(group.getDataRowIds())
					.append("}");
			}
		} else {
			result.append("none");
		}
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("groups", result);
		
		return res;
	}
	
	@GetMapping("/model-group/{pageId}")
	public ResponseData loadModelGroups(@PathVariable String pageId) {
		List<ModelGroup> modelGroupList = this.hdbscanService.getModelGroups(Long.parseLong(pageId));
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("groups", modelGroupList);
		
		return res;
	}
	
	/**
	 * Inspect 화면에서 화면의 요소를 크롤링한 결과로 기존 모델에서 근접한 그룹을 식별한 후 
	 * @param data
	 * @return
	 */
	@GetMapping("/page-predict/{pageId}")
	public ResponseData predictPage(@PathVariable String pageId) {
		List<UiElements> elementList = this.dao.selectUiElementsList(Long.valueOf(pageId));
		
		Map<String, ModelGroup> modelGroupMap = new HashMap<>();
		StringBuilder builder = new StringBuilder();
		
		for(UiElements element : elementList) {
			List<DBSCANGroup> groupList = this.hdbscanService.predict(element);
			
			for(DBSCANGroup group : groupList) {
				String id = group.getId();
				
				ModelGroup modelGroup = modelGroupMap.get(id);
				if(modelGroup == null) {
					try {
						group.printRange(builder, ",");
					} catch (IOException e) {
						e.printStackTrace();
					}
					String rangeTxt = builder.toString();
					builder.delete(0, builder.length());
					
					modelGroup = new ModelGroup();
					modelGroup.setId(id);
					modelGroup.setLabel(group.getLabel());
					modelGroup.setRangeText(rangeTxt);
					
					modelGroupMap.put(id, modelGroup);
				}
				modelGroup.addUiElement(element);
			}
		}		
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("groups", modelGroupMap.values());
		
		return res;
	}
	
	@PostMapping("/group-label")
	public ResponseData updateGroupLabel(@RequestBody JSONObject data) {
		String groupId = data.getAsString("groupId");
		String groupLabel = data.getAsString("groupLabel");
		
		this.hdbscanService.setGroupLabel(groupId, groupLabel);
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		
		return res;
	}











}
