package org.hddbscan.dbscan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


public class ComponentSanitizer {

	public static void main(String[] args) throws Exception {
		final String dir = "C:/tmp/hd-dbscan/";
		
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		
		File[] files = new File(dir).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
			
		});
		for(File file : files) {
		    System.out.println("FileName: " + file.getAbsolutePath());
		    
			JSONObject jsonObj = null;
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				jsonObj = (JSONObject)parser.parse(reader);
			}
			
			JSONObject payload = (JSONObject) jsonObj.get("payload");
			JSONArray targetList = (JSONArray) payload.get("targetList");
			
			Iterator<Object> targetListIter = targetList.iterator();
			while(targetListIter.hasNext()) {
				JSONObject target = (JSONObject)targetListIter.next();
				
				String selector = target.getAsString("selector");
				
				String[] selectors = selector.split(">");
				String lastSelector = selectors[selectors.length - 1];
				
				if(lastSelector.contains(".cl-tabfolder")) {
					System.out.println(selector);
					
//					targetListIter.remove();
				}
			}
		}
		

	}



}
