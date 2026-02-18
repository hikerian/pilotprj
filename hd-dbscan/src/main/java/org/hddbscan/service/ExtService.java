package org.hddbscan.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.minidev.json.JSONObject;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;


@Service
public class ExtService {
	private final Logger log = LoggerFactory.getLogger(ExtService.class);
	
	@Value("${page-component.file.dir}")
	private String pageCompFileDir;
	
	
	public ExtService() {
	}
	
	public String savePageComponentFile(JSONObject data) {
		String pageTitle = data.getAsString("titile");
		
		String fileName = pageTitle.replaceAll("\\s", "");
		String checkFileName = fileName;
		
		File dir = new File(this.pageCompFileDir);
		if(dir.exists() == false) {
			dir.mkdirs();
		} else if(dir.isFile()) {
			throw new RuntimeException(this.pageCompFileDir + " must be directory!");
		}
		
		int idx = 0;
		do {
			if(new File(dir, checkFileName + ".json").exists() == false) {
				break;
			}
			idx++;
			checkFileName = fileName + "(" + idx + ")";
		} while(true);

		File dataFile = new File(dir, checkFileName + ".json");
		
		try (Writer out = new OutputStreamWriter(new FileOutputStream(dataFile), StandardCharsets.UTF_8)) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.serializationConfig().with(SerializationFeature.INDENT_OUTPUT);
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, data);
			
			this.log.debug("writerWithDefaultPrettyPrinter");
		} catch (IOException e) {
			this.log.error("savePageComponentFile error", e);
			throw new RuntimeException(e);
		}
	
		
		return dataFile.getAbsolutePath();
	}
	



}
