package org.hddbscan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.hddbscan.entity.UiElements;
import org.hddbscan.entity.UiPage;
import org.hddbscan.repository.DBAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


@Service
public class StoreService {
	private final Logger log = LoggerFactory.getLogger(StoreService.class);
	
	
	private final DBAccessor dao;
	
	
	public StoreService(DBAccessor dbAccessor) {
		this.dao = dbAccessor;
	}
	
	@Transactional
	public void register(String baseDir) {
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		
		File[] files = new File(baseDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});
		for(File file : files) {
			this.log.debug("Load FileName: {}", file.getAbsolutePath());
			
			JSONObject jsonObj = null;
			try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				jsonObj = (JSONObject)parser.parse(reader);
			} catch (ParseException e) {
				this.log.error("ParseException", e);
			} catch (IOException e) {
				this.log.error("IOException", e);
			}
			
			String title = jsonObj.getAsString("title");
			
			// insert uipage
			long pageId = this.dao.selectNextPageId();
			UiPage uiPage = new UiPage(pageId, title, title);
			this.dao.insertUiPage(uiPage);
			
			this.log.debug(title + " inserted");
			
			JSONObject payload = (JSONObject)jsonObj.get("payload");
			JSONArray targetList = (JSONArray) payload.get("targetList");
			
			for(int i = 0; i < targetList.size(); i++) {
				JSONObject target = (JSONObject)targetList.get(i);
				
				UiElements uiElements = this.dao.convert(pageId, Integer.valueOf(i + 1).longValue(), target);
				
				this.dao.insertUiElements(uiElements);
			}
			
			this.log.debug(title + " ui element {} inserted", targetList.size());
		}

	}


}
