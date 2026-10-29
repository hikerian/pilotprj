package com.hikerian.autotest.record;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hikerian.autotest.record.mapper.ActionMapperFactory;
import com.hikerian.autotest.record.mapper.ActionMapper;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;


public class TestScriptAggregator {
	private final Logger log = LoggerFactory.getLogger(TestScriptAggregator.class);
	
	private final String entrypointUrl;
	private final List<Page> pageList = new CopyOnWriteArrayList<>();
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private BrowserContext context;
	
	
	public TestScriptAggregator(String entrypointUrl) {
		this.entrypointUrl = entrypointUrl;
	}
	
	public String getEntrypoint() {
		return this.entrypointUrl;
	}
	
	public void setBrowserContext(BrowserContext context) {
		this.context = context;
	}
	
	public void pushPage(Page page) {
		this.log.info("pushPage: {}", page);
		
		if(this.pageList.contains(page) == false) {
			this.pageList.add(page);
		}
	}
	
	public void onStateChanged(JsonNode params) {
		this.log.info("onStateChanged: {}", params);
		
		JsonNode pageCount = params.get("pageCount");
		if(pageCount != null) {
			this.log.info("page count: " + params.toPrettyString());
			
			if(this.context != null) {
				List<Page> pageList = this.context.pages();
				this.log.info("context pages: {}", pageList);
				
				List<BrowserContext> contextList = this.context.browser().contexts();
				this.log.info("contexts: {}", contextList);
				
				for(Page page : pageList) {
					if(this.pageList.contains(page) == false) {
						this.pageList.add(page);
					}
				}
			}
		}
	}
	
	public void onInspectRequested(JsonNode params) {
		this.log.warn("onInspectRequested: {}", params);
	}
	
	public void onSourceChanged(JsonNode params) {
		this.log.info("onSourceChanged: {}", params);
		
		JsonNode actionInContext = params.get("aiqbot"); // playwright debugcontroller의 ActionInContext 배열, actionInContext.getNodeType() : STRING
		
		String actionInContextStr = actionInContext.asText();
		
		try {
			actionInContext = this.objectMapper.readTree(actionInContextStr); // actionInContext.getNodeType() : ARRAY
		} catch (JsonProcessingException e) {
			this.log.error("ActionInContext ParsingError: {}", e);
			throw new RuntimeException(e);
		}
		
		int actionCount = actionInContext.size();
		this.log.info("number of actions: {}", actionCount);
		
//		if(actionCount > 10) { // for debugging
//			String node = actionInContext.toPrettyString();
//			System.out.println(node);
//		}
		
		for(int i = 0; i < actionCount; i++) {
			/* export type ActionInContext = {
			 *   pageGuid: string;
			 *   action: Action;
			 *   signals: Signal[];
			 * };
			 */
			JsonNode actionDesc = actionInContext.get(i);
			String pageGuid = actionDesc.get("pageGuid").asText();
			JsonNode action = actionDesc.get("action");
			JsonNode signals = actionDesc.get("signals");
			
			// 'check'|'click'|'hover'|'closePage'|'fill'|'navigate'|'openPage'|'press'|'select'|'uncheck'|'setInputFiles'|'assertText'|'assertValue'|'assertChecked'|'assertVisible'|'assertSnapshot'
			final String actionName = action.get("name").asText();
			
			ActionMapperFactory mapperFactory = ActionMapperFactory.getInstance();
			ActionMapper mapper = mapperFactory.getBuilder(actionName);
			
			mapper.map(pageGuid, action, signals, this);
		}
	}

	public void onPaused(JsonNode params) {
		this.log.warn("onPaused: {}", params);
		
	}

	public void onSetModeRequested(JsonNode params) {
		this.log.warn("onSetModeRequested: {}", params);
		
	}
	
	public String finish() {
		// TODO implements..
		return "Test script...";
	}

}
