package com.hikerian.autotest.record.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ActionMapperFactory {
	private static ActionMapperFactory instance = new ActionMapperFactory();
	
	
	public static ActionMapperFactory getInstance() {
		return ActionMapperFactory.instance;
	}
	
	/**
	 * Playwright에서 수신하는 ActionNames
	 * - check
	 * - click
	 * - hover
	 * - closePage
	 * - fill
	 * - navigate
	 * - openPage
	 * - press
	 * - select
	 * - uncheck
	 * - setInputFiles
	 * - assertText
	 * - assertValue
	 * - assertChecked
	 * - assertVisible
	 * - assertSnapshot
	 **/
	private final Map<String, ActionMapper> mapperMap = new ConcurrentHashMap<>();
	private final ActionMapper dummy = new DummyActionMapper(); // 지원하지 않는 action은 skip!!
	
	private ActionMapperFactory() {
		this.init();
	}
	
	private void init() {
		this.addBuilder(new ClickActionMapper());
		// TODO ... action 별 빌더 추가
		
		
	}
	
	private void addBuilder(ActionMapper mapper) {
		String actionName = mapper.getActionName();
		this.mapperMap.put(actionName, mapper);
	}
	
	public ActionMapper getBuilder(String actionName) {
		ActionMapper mapper = this.mapperMap.get(actionName);
		
		if(mapper == null) { // 지원하지 않는 action은 skip!!
			mapper = this.dummy;
		}
		return mapper;
	}


}
