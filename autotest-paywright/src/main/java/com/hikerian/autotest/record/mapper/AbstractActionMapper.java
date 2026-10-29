package com.hikerian.autotest.record.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.hikerian.autotest.record.TestScriptAggregator;


public abstract class AbstractActionMapper implements ActionMapper {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	protected AbstractActionMapper() {
	}
	
	public abstract String getActionName();
	public abstract void map(String pageGuid, JsonNode action, JsonNode signals, TestScriptAggregator aggregator);

}
