package org.hddbscan.controller;

import org.hddbscan.service.ExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/rest")
public class ExtController {
	private final Logger log = LoggerFactory.getLogger(ExtController.class);
	
	private final ExtService extService;
	
	
	public ExtController(ExtService extService) {
		this.extService = extService;
	}
	
	@PostMapping("/page-components")
	public ResponseData pageComponents(@RequestBody JSONObject data) {
		this.log.debug("received data: {}", data);
		
		String filePath = this.extService.savePageComponentFile(data);
		
		ResponseData res = new ResponseData();
		res.setSuccess(true);
		res.addPayload("filePath", filePath);
		
		
		return res;
	}

}
