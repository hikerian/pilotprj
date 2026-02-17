package org.hddbscan.dbscan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.hddbscan.dbscan.service.UIElementDataSet;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


public class HDBSCANTest {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		
		final String dir = "C:/tmp/hd-dbscan/";
		
		File logFile = new File(dir, System.currentTimeMillis() + "-log.txt");
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));) {
		
		
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

		UIElementDataSet uiDataSet = new UIElementDataSet();
//		List<String> classNameList = new ArrayList<>();
//		List<String> textList = new ArrayList<>();
		
		File[] files = new File(dir).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
			
		});
		for(File file : files) {
//		    File file = files[0];
		    
		    System.out.println("FileName: " + file.getAbsolutePath());
		    
			JSONObject jsonObj = null;
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				jsonObj = (JSONObject)parser.parse(reader);
			}
			
			String title = jsonObj.getAsString("titile");
			JSONObject payload = (JSONObject) jsonObj.get("payload");
			JSONArray targetList = (JSONArray) payload.get("targetList");
			
			for(int i = 0; i < targetList.size(); i++) {
				JSONObject target = (JSONObject)targetList.get(i);
				
				uiDataSet.addRow(title + (i + 1), target);
				
//				JSONArray classNameArray = (JSONArray) target.get("classNames");
//				String[] classNms = classNameArray.toArray(new String[0]);
//				
//				for(String classNm : classNms) {
//					if(classNameList.contains(classNm) == false) {
//						classNameList.add(classNm);
//					}
//				}
//				
//				String text = target.getAsString("text");
//				if(textList.contains(text) == false) {
//					textList.add(text);
//				}
				
			}
		}
		
//		System.out.println("==============================");
//		System.out.println(classNameList);
//		System.out.println("==============================");
//		System.out.println(textList);
//		System.out.println("==============================");
		
		final String delimiter = "\t";
			
//		out.println("==============================");
//		uiDataSet.print(out, delimiter);
				
		DataSet dataSet = uiDataSet.toDataSet();
		DBSCANMetadata metadata = uiDataSet.getMetadata();
		
		out.println("==============================");
		dataSet.print(out, delimiter);
		
		HDBSCAN hbscan = new HDBSCAN();
		hbscan.setMetadata(metadata);
		DBSCANModel model = hbscan.fit(dataSet);
		
		out.println("==============================");
		model.print(out, delimiter);
		
		out.flush();
		}
		
		System.out.println("Spent " + (System.currentTimeMillis() - startTime) + " ms.");
		
	}



}
