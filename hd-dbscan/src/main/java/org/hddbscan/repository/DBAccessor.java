package org.hddbscan.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hddbscan.entity.UiElements;
import org.hddbscan.entity.UiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


@Repository
public class DBAccessor {
	private final Logger log = LoggerFactory.getLogger(DBAccessor.class);
	
	private final SQLMapper sql;
	
	private final NamedParameterJdbcOperations jdbcTemplate;
	
	private final BeanPropertyRowMapper<UiPage> uiPageRowMapper = BeanPropertyRowMapper.newInstance(UiPage.class);
	private final BeanPropertyRowMapper<UiElements> uiElementsRowMapper = BeanPropertyRowMapper.newInstance(UiElements.class);


	public DBAccessor(NamedParameterJdbcTemplate jdbcTemplate) throws IOException {
		this.jdbcTemplate = jdbcTemplate;
		this.sql = new SQLMapper();
	}
	
	private <T> SqlParameterSource getParameterSource(T item) {
		return new BeanPropertySqlParameterSource(item);
	}
	
	public long selectNextPageId() {
		this.log.debug("selectNextPageId");
		
		Long nextPageId = this.jdbcTemplate.queryForObject(this.sql.getSQL("nextPageIdQuery"), Collections.emptyMap(), Long.class);
		
		return nextPageId;
	}
	
	public int insertUiPage(UiPage uiPage) {
		this.log.debug("insertUiPage: {}", uiPage);
		
		SqlParameterSource uiPageParam = this.getParameterSource(uiPage);

		return this.jdbcTemplate.update(this.sql.getSQL("uiPageInsert"), uiPageParam);
	}

	public int insertUiElements(UiElements uiElements) {
		SqlParameterSource uiElementsParam = this.getParameterSource(uiElements);

		return this.jdbcTemplate.update(this.sql.getSQL("uiElementsInsert"), uiElementsParam);
	}
	
	public List<UiPage> selectUiPageList() {
		return this.jdbcTemplate.query(this.sql.getSQL("uiPageQuery"), Collections.emptyMap(), this.uiPageRowMapper);
	}
	
	public List<UiElements> selectUiElementsList(long pageId) {
		Map<String, Object> param = new HashMap<>();
		param.put("pageId", pageId);

		return this.jdbcTemplate.query(this.sql.getSQL("uiElementsQuery"), param, this.uiElementsRowMapper);
	}
	
	public UiElements selectUiElements(long pageId, long elementId) {
		Map<String, Object> param = new HashMap<>();
		param.put("pageId", pageId);
		param.put("elementId", elementId);
		
		return this.jdbcTemplate.queryForObject(this.sql.getSQL("uiElementQuery"), param, this.uiElementsRowMapper);
	}
	
	public UiElements convert(long pageId, long elementId, JSONObject target) {
		UiElements element = new UiElements();
		
		String selector = target.getAsString("selector");
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		String classNms = String.join(",", classNameArray.toArray(new String[0]));
		String text = target.getAsString("text");
		
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		double left = clientRect.getAsNumber("left").doubleValue();
		double top = clientRect.getAsNumber("top").doubleValue();
		double width = clientRect.getAsNumber("width").doubleValue();
		double height = clientRect.getAsNumber("height").doubleValue();
		boolean major = (boolean) target.get("major");
		
		element.setPageId(pageId);
		element.setElementId(elementId);
		element.setSelectorText(selector);
		element.setClassNames(classNms);
		element.setCtntText(text);
		element.setPosLeft(left);
		element.setPosTop(top);
		element.setUiWidth(width);
		element.setUiHeight(height);
		element.setMajorYn(major ? "Y" : "N");
		
		return element;
	}


}
