package com.hikerian.autotest.record;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * http://211.109.22.33:8791/index.do
 */
@Service
public class TestScriptRecordService {
	private final Logger log = LoggerFactory.getLogger(TestScriptRecordService.class);
	
	private String sessionId;
	private TestScriptRecordProcessor processor;
	private TestScriptAggregator aggregator;
	
	
    public static void main(String[] args) throws Exception {
    	TestScriptRecordService client = new TestScriptRecordService();
    	
    	String uuid = client.record();
    	
        System.out.println();
        System.out.println("================================");
        System.out.println("Browser에서 동작을 수행하세요.");
        System.out.println("Enter를 누르면 종료합니다.");
        System.out.println("================================");
        System.in.read();
        
        String script = client.finish(uuid);
        
        System.out.println(script);
    }
	
	public TestScriptRecordService() {
	}
	
	public String record() throws AlreadyExistsException {
		if(this.sessionId != null) {
			throw new AlreadyExistsException("Already used");
		}
		
		TestScriptRecordProcessor processor = new TestScriptRecordProcessor();
		this.processor = processor;

		TestScriptAggregator aggregator = new TestScriptAggregator();
		this.aggregator = aggregator;
		
		// Playwright Instance를 미리 확보하여 소유권 획득
		try {
			processor.installChromium();
			
			processor.startPlaywrightServer();
			
			RecordingWebSocketHandler wsHandler = new RecordingWebSocketHandler(aggregator);
			processor.connectBrowserEventClient(wsHandler);
			
			processor.initializeBrowserEventListening();
			
			processor.startRecording();
			
			BrowserEventHandler brwsHandler = new BrowserEventHandler(aggregator);
			processor.connectBrowserClient(brwsHandler);
		} catch(Exception ex) {
//			TODO 예외처리
			ex.printStackTrace();
		}
		
		UUID sessionUUID = UUID.randomUUID();
		
		this.sessionId = sessionUUID.toString();
		
		return sessionId;
	}
	
	public String finish(String sessionId) {
		if(this.processor == null
				|| this.sessionId == null
				|| this.sessionId.equals(sessionId) == false) {
			return null;
		}
		
		try {
			this.processor.stopRecording();
		} catch (Exception e) {
//			TODO 예외처리
			e.printStackTrace();
		}
		
		this.processor = null;
		this.sessionId = null;
		
		String testScript = this.aggregator.finish();
		this.aggregator = null;
		
		return testScript;
		
	}
	
	
	
	


}
