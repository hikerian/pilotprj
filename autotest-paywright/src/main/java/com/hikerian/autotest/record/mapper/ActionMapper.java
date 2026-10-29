package com.hikerian.autotest.record.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hikerian.autotest.record.TestScriptAggregator;


/**
 * Playwright Action을 AIQBot TestCommand로 변경하는 Mapper Interface.
 */
public interface ActionMapper {
	/**
	 * 처리하는 action 이름
	 * 'check'|'click'|'hover'|'closePage'|'fill'|'navigate'|'openPage'|'press'|'select'|'uncheck'|'setInputFiles'|'assertText'|'assertValue'|'assertChecked'|'assertVisible'|'assertSnapshot'
	 * @return
	 */
	public String getActionName();
	
	/**
	 * Playwright에서 받은 정보를 이용하여 AIQBot의 TestAction을 생성하고 TestScript에 추가.
	 * TestScript는 Aggregator를 이용.
	 * @param pageGuid
	 * @param action
	 * @param signals
	 * @param aggregator
	 */
	public void map(String pageGuid, JsonNode action, JsonNode signals, TestScriptAggregator aggregator);
}
