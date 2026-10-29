package com.hikerian.autotest.record.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hikerian.autotest.record.TestScriptAggregator;


/**
 * 지원하지 않는 action의 경우 아무처리도 하지 않음.
 */
public class DummyActionMapper extends AbstractActionMapper {
	
	public DummyActionMapper() {
	}

	@Override
	public String getActionName() {
		return "dummy";
	}

	@Override
	public void map(String pageGuid, JsonNode action, JsonNode signals, TestScriptAggregator aggregator) {
		// Do nothing
		
	}

}
