package com.hikerian.autotest.record.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hikerian.autotest.record.TestScriptAggregator;


/**
 * Playwright의 click action 정보를 이용하여 AIQBot TestCommand를 생성하는 클래스.
 */
public class ClickActionMapper extends AbstractActionMapper {
	
	
	public ClickActionMapper() {
	}

	@Override
	public String getActionName() {
		return "click";
	}

	@Override
	public void map(String pageGuid, JsonNode action, JsonNode signals, TestScriptAggregator aggregator) {
		// TODO Auto-generated method stub
		
		this.log.info("action: {}\n pageGuid: {}\n action: {}\n signals: {}"
				, this.getActionName()
				, pageGuid, action.toPrettyString()
				, signals.toPrettyString());
		
	}

}
