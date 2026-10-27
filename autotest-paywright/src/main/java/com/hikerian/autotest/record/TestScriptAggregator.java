package com.hikerian.autotest.record;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.hikerian.autotest.record.js.JsParser;
import com.hikerian.autotest.record.js.JsParser.FuncCall;
import com.hikerian.autotest.record.js.JsParser.VariableName;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;


public class TestScriptAggregator {
	private final Logger log = LoggerFactory.getLogger(TestScriptAggregator.class);
	
	private final JsParser jsParser = JsParser.getInstance();
	
	private final String entrypointUrl;
	private final List<Page> pageList = new CopyOnWriteArrayList<>();
	
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
		
		JsonNode actionList = params.get("actions");
		
		JsonNode lastAction = actionList.get(actionList.size() - 1);
		
		String actionText = lastAction.asText();
		
		FuncCall funcCall = this.jsParser.parse(actionText);
		String funcName = funcCall.getName();
		if("goto".equals(funcName)) {
			VariableName varNm = this.jsParser.parseVarName(funcCall.getTarget());
			int seq = varNm.getSeq();
			
			if(this.pageList.size() > seq) {
				Page page = this.pageList.get(seq);
				String title = page.title();
				
				this.log.info("page title: {}" + title);
			} else {
				this.log.error("Page Not Found");
			}
		}
		
		// TODO translation to TestScript
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
