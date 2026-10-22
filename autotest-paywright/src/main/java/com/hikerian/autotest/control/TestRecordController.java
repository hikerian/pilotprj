package com.hikerian.autotest.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hikerian.autotest.record.AlreadyExistsException;
import com.hikerian.autotest.record.TestScriptRecordService;

import jakarta.servlet.http.HttpSession;


/**
 * http://211.109.22.33:8791/index.do
 */
@RestController
@RequestMapping("/record")
public class TestRecordController {
	private final Logger log = LoggerFactory.getLogger(TestRecordController.class);
	
	private final TestScriptRecordService testScriptRecordService;
	
	
	public TestRecordController(TestScriptRecordService testScriptRecordService) {
		this.testScriptRecordService = testScriptRecordService;
	}
	
	@GetMapping("/start")
	public String start(HttpSession session) {
		String sessionId;
		
		try {
			sessionId = this.testScriptRecordService.record();
		} catch (AlreadyExistsException e) {
			e.printStackTrace();
			return "Already Recording...";
		}
		
		this.log.debug("sessionId: {}", sessionId);
		
		session.setAttribute("sessionId", sessionId);
		
		return sessionId;
	}
	
	@GetMapping("/stop")
	public String stop(HttpSession session) {
		String sessionId = (String)session.getAttribute("sessionId");
		
		this.log.debug("sessionId: {}", sessionId);
		
		String script = this.testScriptRecordService.finish(sessionId);
		
		return "Recording stopped: " + script;
		
	}
	
	

}
