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

import org.hddbscan.entity.UiElements;
import org.hddbscan.preprocessing.RawCluster;
import org.hddbscan.service.conv.DataSetConverter;
import org.hddbscan.service.conv.DataSetConverterMetadata;

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
	
			DataSetConverter converter = new DataSetConverter();
			
			File[] files = new File(dir).listFiles(new FilenameFilter() {
	
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}
				
			});
			
			RawCluster<UiElements> cluster = new RawCluster<>("other");
			long pageId = 0;
			for(File file : files) {
				pageId++;
			    
			    System.out.println("FileName: " + file.getAbsolutePath());
			    
				JSONObject jsonObj = null;
				try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
					jsonObj = (JSONObject)parser.parse(reader);
				}
				
				JSONObject payload = (JSONObject) jsonObj.get("payload");
				JSONArray targetList = (JSONArray) payload.get("targetList");
				
				for(int i = 0; i < targetList.size(); i++) {
					JSONObject target = (JSONObject)targetList.get(i);
					
					cluster.add(DataSetConverter.convert(pageId, (i + 1), target));
					
				}
			}
			converter.addCluster(cluster);
		
			final String delimiter = "\t";
				
			DataSetConverterMetadata meta = DataSetConverter.genType1Meta();
	
			DataSet dataSet = converter.genDataSet(meta);
			DBSCANMetadata metadata = converter.genDBSCANMetadata(meta);
			
			HDBSCAN hbscan = new HDBSCAN();
			hbscan.setMetadata(metadata);
			DBSCANModel model = hbscan.fit(dataSet);
			
			out.println("==============================");
			model.print(out, delimiter);
			
			System.out.println("Spent " + (System.currentTimeMillis() - startTime) + " ms.");
			
			out.flush();
		}
		
	}



}
