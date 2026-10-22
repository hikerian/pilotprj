package com.hikerian.autotest.record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.Driver;


public class TestScriptRecordProcessor implements AutoCloseable {
	private final Logger log = LoggerFactory.getLogger(TestScriptRecordProcessor.class);
	
	private final Playwright playwright;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private Process playwrightServer;
	private String wsEndpoint;
	private WebSocketSession browserEventWsSession;
	private Browser browser;
	
	
	public TestScriptRecordProcessor() throws AlreadyExistsException {
		PlaywrightManager manager = PlaywrightManager.getInstance();
		Playwright playwright = manager.createPlaywright();
		this.playwright = playwright;
	}
	
	/**
	 * CLI를 이용하여 Chromium을 설치.
	 * 테스트를 위한 구현으로 배포판에서는 제거되거나 EnterpriseTestManager를 통해 다운로드 받는 식으로 변경되어야 함. 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void installChromium() throws IOException, InterruptedException {
		Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
		ProcessBuilder builder = driver.createProcessBuilder();
		
		String[] args = {"install", "chromium"};
		
		builder.command().addAll(Arrays.asList(args));
		
		this.log.debug("cmd: {}, env: {}", builder.command(), builder.environment());
		
		builder.inheritIO();
		
		Process process = builder.start();
		int exitCode = process.waitFor();
		
		if(exitCode != 0) {
			throw new IllegalStateException("Chromium installation failed: " + exitCode);
		}
	}
	
	public void startPlaywrightServer() throws IOException {
		Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
		ProcessBuilder builder = driver.createProcessBuilder();
		
    	/*
    	 * --mode:
    	 * ㆍdefault: 일반 Playwright WebSocket 서버로 동작하며, 클라이언트 연결에 사용할 브라우저를 실행하고 원격 브라우저 자동화에 사용
    	 * ㆍextension: 브라우저 재사용 및 DebugController 연결을 지원하는 모드로 Recorder, Inspector, VS Code 연동시 사용
    	 */
		// TODO 상수 처리
		String[] args = {
				"run-server",
    			"--host", "127.0.0.1",
                "--mode", "extension",
                "--path", "/java-recorder"
		};
		builder.command().addAll(Arrays.asList(args));
		
		Map<String, String> env = builder.environment();
		env.put("PW_CODEGEN_NO_INSPECTOR","1"); // 별도 Inspector window를 띄우지 않는다.
		env.put("PW_EXTENSION_MODE","1"); // extension/debug-controller 형태로 동작하도록 한다.
		
		/*
		 * 이 속성이 참(true)이면, 이 객체의 start() 메서드를 통해 이후에 시작되는 하위 프로세스에서 발생하는 모든 오류 출력은 표준 출력과 병합됩니다.
		 * 따라서 Process.getInputStream() 메서드를 사용하여 이 두 출력을 모두 읽을 수 있게 됩니다.
		 * 이를 통해 오류 메시지와 그에 대응하는 출력을 더 쉽게 연관 지어 확인할 수 있습니다.
		 * 초기값은 거짓(false)입니다.
		 */
		builder.redirectErrorStream(true);
		
		this.playwrightServer = builder.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.playwrightServer.getInputStream()));
		for(String line; (line = reader.readLine()) != null;) {
            /*
             * Playwright run-server 출력 예:
             * Listening on ws://127.0.0.1:53219/java-recorder
             */
			this.log.debug("Playwright Server Message: {}", line);
			
			if(line.startsWith("Listening on ")) {
				String wsEndpoint = line.substring("Listening on ".length()).trim();
				this.log.info("Playwright Recording Server Endpoint: {}", wsEndpoint);
				
				this.wsEndpoint = wsEndpoint;
				
				return;
			}
		}
		
		throw new IllegalStateException("Playwright WebSocket endpoint not found.");
	}
	
	public void connectBrowserEventClient(RecordingWebSocketHandler handler) throws InterruptedException, ExecutionException {
		// websocket connection url: ws://.../java-recorder?debug-controller
		String debugEndpoint = this.wsEndpoint + "?debug-controller"; // TODO 상수로 수정
		
		this.log.info("Browser Event Client Connect url: {}", debugEndpoint);
		
		StandardWebSocketClient client = new StandardWebSocketClient();
		CompletableFuture<WebSocketSession> wsConnFuture = client.execute(
				handler
				, new WebSocketHttpHeaders()
				, URI.create(debugEndpoint));
		
		this.browserEventWsSession = wsConnFuture.get();
		
		this.log.info("Browser Event Client Connected.");
	}
	
	public void initializeBrowserEventListening() throws InterruptedException, ExecutionException {
		ObjectNode params = this.objectMapper.createObjectNode();
		// TODO 상수화
		params.put("codegenId", "playwright-test");
        params.put("sdkLanguage", "java");
        
        JsonNode result = this.sendByEventSession("initialize", params).get();
        
        this.log.debug("initialize completed: {}", result);
        
        // VS Code extension 역시 initialize 이후 setReportStateChanged(true)를 호출한다.
        params = this.objectMapper.createObjectNode();
        params.put("enabled", true);
        
        result = this.sendByEventSession("setReportStateChanged", params).get();
        
        this.log.debug("setReportStateChanged completed: {}", result);
	}
	
	public void startRecording() throws InterruptedException, ExecutionException {
        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "recording");

        /*
         * 필요하다면:
         * params.put("testIdAttributeName", "data-testid");
         */
        JsonNode result = this.sendByEventSession("setRecorderMode", params).get();

        this.log.info("Recorder started: {}", result);
	}
	
	public void stopRecording() throws InterruptedException, ExecutionException {
        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "none");

        JsonNode result = this.sendByEventSession("setRecorderMode", params).get();

        this.log.info("Recorder stopped: {}", result);
	}
	
	public void connectBrowserClient(BrowserEventHandler handler)  {
//		this.browser = this.playwright.chromium().connect(this.wsEndpoint + "?connect=first&browser=chromium");
		this.browser = this.playwright.chromium().connect(this.wsEndpoint + "?connect=first");
//		this.browser = this.playwright.chromium().connect(this.wsEndpoint);
		
		List<BrowserContext> contextList = this.browser.contexts();
		// Context 및 Page 단위 이벤트를 감지하기 위해 Handler 등록
		for(BrowserContext context : contextList) {
			handler.init(context);
		}
		
		this.log.debug("Browser Connected");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private CompletableFuture<JsonNode> sendByEventSession(String method, JsonNode params) {
		return null;
	}

	@Override
	public void close() throws Exception {
		if(this.browserEventWsSession != null && this.browserEventWsSession.isOpen()) {
			this.browserEventWsSession.close();
		}
		if(this.browser != null) {
			this.browser.close();
		}
		if(this.playwright != null) {
			this.playwright.close();
		}
		if(this.playwrightServer != null) {
			this.playwrightServer.destroy();
			this.playwrightServer = null;
		}
		
	}
	
	








}
