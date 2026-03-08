package org.hddbscan.dbscan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hddbscan.preprocessing.RawCluster;
import org.hddbscan.preprocessing.UIElementRaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


public class StructuralClustering {
	private static final String dir = "C:/tmp/hd-dbscan/";
	
	private final Logger log = LoggerFactory.getLogger(StructuralClustering.class);
	
	private final String[] replace = {
		"^body > div.cl-control.cl-container:nth-of-type\\([\\d+]\\)"
			, "body > div.cl-control.cl-container"
	};
	
	private final String[] groupClasses = {
		"search-box", "data-box", "form-box"
		// 셋 중 하나의 가장 마지막 selector의 마지막 인덱스를 기준으로 그룹을 끊음.
	};
	
	
	public StructuralClustering() {
		((ch.qos.logback.classic.Logger)this.log).setLevel(ch.qos.logback.classic.Level.DEBUG);
	}
	
	private File[] getDataFiles(String dir) {
		File[] files = new File(dir).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
			
		});
		
		return files;
	}
	
	private List<UIElementRaw> loadElementDataSet(File[] files) throws Exception {
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		List<UIElementRaw> rawList = new ArrayList<>();
		
		int pageId = 1;
		for(File file : files) {
			this.log.debug("file: {}", file.getAbsolutePath());
			
			JSONObject pageObj = null;
			
			try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				pageObj = (JSONObject)parser.parse(reader);
			}
			
			JSONObject payload = (JSONObject)pageObj.get("payload");
			JSONArray targetList = (JSONArray)payload.get("targetList");
			
			for(int i = 0; i < targetList.size(); i++) {
				JSONObject target = (JSONObject)targetList.get(i);
				
				// selector 일반화...
				String selector = target.getAsString("selector");
				String newSelector = selector.replaceFirst(this.replace[0], this.replace[1]);
				
				target.put("selector", newSelector);
				
				rawList.add(new UIElementRaw(String.format("%1$d:%2$d", pageId, (i + 1)), target));
			}
			
			pageId++;
		}
		
		return rawList;
	}
	
	private void preclustering(List<UIElementRaw> rawList) {
		long start = System.currentTimeMillis();
		
		// Selector를 기준으로 정렬
		rawList.sort((UIElementRaw raw1, UIElementRaw raw2) -> {
			return raw1.getSelector().compareTo(raw2.getSelector());
		});
		
		// tree의 그룹요소를 1차원 cluster로 펼치기..
		Map<String, RawCluster> clusterMap = new HashMap<>();
		RawCluster other = new RawCluster("other");
		clusterMap.put("other", other);
		
		int maxCnt = 0;
		elementLoop: for(UIElementRaw raw : rawList) {
			maxCnt++;
			String selector = raw.getSelector();
			
//			this.log.debug("Selector: {}", selector);
			
			String[] selectors = selector.split("\\s+>\\s+");
			for(int i = selectors.length - 1; i >= 0; i--) {
				String sel = selectors[i];
				
				for(String className : this.groupClasses) {
					if(sel.contains(className)) {
						String clusterId = this.genClusterId(selectors, i);
						
						RawCluster cluster = clusterMap.get(clusterId);
						if(cluster == null) {
							cluster = new RawCluster(clusterId);
							clusterMap.put(clusterId, cluster);
						}
						cluster.add(raw);
						
						continue elementLoop;
					}
				}				
			}
			other.add(raw);
			
//			if(maxCnt > 100) {
//				break;
//			}
		}
		
		this.log.info("Preprocessing Cluster count: {}, Raw count: {}, time: {} ms spent", clusterMap.size(), maxCnt, System.currentTimeMillis() - start);
		
		clusterMap.values().forEach((cluster) -> {
			this.log.debug("ClusterId: {}, Element Count: {}", cluster.getId(), cluster.elementCount());
		});
	}
	
	private String genClusterId(String[] splitedSelector, int maxIdx) {
		String[] newSelector = new String[maxIdx + 1];
		System.arraycopy(splitedSelector, 0, newSelector, 0, maxIdx + 1);
		
		return String.join(" > ", newSelector);
	}
	
	public static void main(String[] args) throws Exception {
		StructuralClustering cluster = new StructuralClustering();
		File[] files = cluster.getDataFiles(StructuralClustering.dir);
		List<UIElementRaw> rawList = cluster.loadElementDataSet(files);
		
		cluster.preclustering(rawList);
		

		
//		String selector = "body > div.cl-control.cl-container:nth-of-type(3) > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.body.cl-control.cl-container:nth-of-type(4) > div.cl-layout > div.cl-layout-content > div.cl-control.cl-tabfolder.cl-mdifolder.tab-content:nth-of-type(1) > div.content > div.cl-tabfolder-body:nth-of-type(2) > div:nth-of-type(2) > div.cl-control.cl-embeddedapp > div.cl-control.cl-container > div.cl-layout.cl-scrollbar:nth-of-type(1) > div.cl-layout-content > div.cl-first-row.cl-layout-wrap:nth-of-type(2) > div.cl-control.cl-embeddedapp.cl-first-row > div.cl-control.cl-container > div.cl-layout:nth-of-type(1) > div.cl-layout-content > div.cl-control.cl-output.cl-selectable.cl-writingmode-horizontal-tb:nth-of-type(2)";
//		String pattern = "^body > div.cl-control.cl-container:nth-of-type\\([\\d+]\\)";
//		
//		String newSelector = selector.replaceAll(pattern, "body > div.cl-control.cl-container");
//		
//		System.out.println(selector);
//		System.out.println(newSelector);
		
		
		
		
	}

	

	

}
