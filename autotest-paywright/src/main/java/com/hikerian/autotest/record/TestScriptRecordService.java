package com.hikerian.autotest.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microsoft.playwright.Playwright;


@Service
public class TestScriptRecordService {
	private final Logger log = LoggerFactory.getLogger(TestScriptRecordService.class);
	
	
	public TestScriptRecordService() {
	}
	
	public String record() throws AlreadyExistsException {
		// Playwright Instance를 미리 확보하여 소유권 획득
		try(TestScriptRecordProcessor processor = new TestScriptRecordProcessor()) {
			
			
			processor.installChromium();
			
			processor.startPlaywrightServer();
			
			RecordingWebSocketHandler wsHandler = new RecordingWebSocketHandler();
			processor.connectBrowserEventClient(wsHandler);
			
			processor.initializeBrowserEventListening();
			
			processor.startRecording();
			
			BrowserEventHandler brwsHandler = new BrowserEventHandler();
			processor.connectBrowserClient(brwsHandler);
			
			
			// TODO 세션 관리 추가
			// TODO Stop Recording 처리
		} catch(AlreadyExistsException ex) {
			throw ex;
		} catch(Exception ex) {
//			TODO 예외처리
		}
		
		
		
		
		
		return "";
	}
	
	
	
	


}
