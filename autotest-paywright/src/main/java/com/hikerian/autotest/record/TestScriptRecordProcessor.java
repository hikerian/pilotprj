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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.Driver;


/**
 * Browser를 사용해야만 Browser 이벤트를 수신할 수 있는 듯 하다!!
 */
public class TestScriptRecordProcessor {
	private final Logger log = LoggerFactory.getLogger(TestScriptRecordProcessor.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private final AtomicLong reqId = new AtomicLong();
	private final Map<Long, CompletableFuture<JsonNode>> pendingReqs = new ConcurrentHashMap<>();
	
//	private final Playwright playwright;
	private Playwright playwright;
	
	private Process playwrightServer;
	private String wsEndpoint;
	private WebSocketSession browserEventWsSession;
	private Browser browser;
	
	
	public TestScriptRecordProcessor() throws AlreadyExistsException {
//		PlaywrightManager manager = PlaywrightManager.getInstance();
//		Playwright playwright = manager.createPlaywright();
//		this.playwright = playwright;
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
		
		this.log.info("cmd: {}, env: {}", builder.command(), builder.environment());
		
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
		
		builder.redirectErrorStream(true);
		
		this.log.info("Server Cmd: {}, env: {}", builder.command(), builder.environment());
		
		this.playwrightServer = builder.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.playwrightServer.getInputStream()));
		for(String line; (line = reader.readLine()) != null;) {
            /*
             * Playwright run-server 출력 예:
             * Listening on ws://127.0.0.1:53219/java-recorder
             */
			this.log.info("Playwright Server Message: {}", line);
			
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
				this.new BrowserEventWebSocketHandler(handler)
//				, new WebSocketHttpHeaders()
				, null
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
        
        this.log.info("initialize completed: {}", result);
        
        // VS Code extension 역시 initialize 이후 setReportStateChanged(true)를 호출한다.
        params = this.objectMapper.createObjectNode();
        params.put("enabled", true);
        
        result = this.sendByEventSession("setReportStateChanged", params).get();
        
        this.log.info("setReportStateChanged completed: {}", result);
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
	
	private BrowserEventHandler handler;
	
	public void connectBrowserClient(BrowserEventHandler handler) throws AlreadyExistsException  {
		/*
		this.handler = handler;
		
		PlaywrightManager manager = PlaywrightManager.getInstance();
		Playwright playwright = manager.createPlaywright();
		this.playwright = playwright;
//		this.playwright = Playwright.create();
		
//		this.browser = this.playwright.chromium().connect(this.wsEndpoint + "?connect=first&browser=chromium");
		this.browser = this.playwright.chromium().connect(this.wsEndpoint + "?connect=first");
//		this.browser = this.playwright.chromium().connect(this.wsEndpoint);
		
		List<BrowserContext> contextList = this.browser.contexts();
		// Context 및 Page 단위 이벤트를 감지하기 위해 Handler 등록
		for(BrowserContext context : contextList) {
//			handler.init(context);
//			
//			context.onRequest(req -> {
//				System.out.println("---------------------------");
//				handler.onRequest(req);
//	    	});
			
	    	context.onPage(page -> {
	    		System.out.println("###### Init New Page: " + page.url());
	    	});
	    	context.onPageLoad(page -> {
	    		System.out.println("###### Init Page Load: " + page.url());
	    	});
	    	context.onPageClose(page -> {
	    		System.out.println("###### Init Page Closed: " + page.url());
	    	});
	    	context.onClose(ctx -> {
	    		System.out.println("###### Init Context Closed: " + ctx);
	    	});
	    	context.onDialog(dialog -> {
	    		System.out.println("###### Init Dialog Load: " + dialog.message());
	    		dialog.accept();
	    	});
	    	context.onRequest(req -> {
	    		System.out.println("###### Init Request: " + req.url() + ":" + req + ":" + req.frame().page());
	    	});
	    	context.onResponse(res -> {
	    		System.out.println("###### Init Response: " + res.url() + ":" + res + ":" + res.frame().page());
	    		
	    	});
		}
		
		this.log.info("Browser Connected");
		*/
		
        this.playwright = Playwright.create();
//      this.browser = this.playwright.chromium().connect(wsEndpoint + "?connect=first&browser=chromium");
      this.browser = this.playwright.chromium().connect(this.wsEndpoint + "?connect=first");
//      this.browser = this.playwright.chromium().connect(wsEndpoint);

      List<BrowserContext> contextList = this.browser.contexts();
      
      // Context 및 Page 단위 이벤트를 감지하기 위해 Handler 등록
      System.out.println("@@@@@@ BrowserContextList: " + contextList);
      for(BrowserContext context : contextList) {
      	context.onPage(page -> {
      		System.out.println("###### Init New Page: " + page.url());
      	});
      	context.onPageLoad(page -> {
      		System.out.println("###### Init Page Load: " + page.url());
      	});
      	context.onPageClose(page -> {
      		System.out.println("###### Init Page Closed: " + page.url());
      	});
      	context.onClose(ctx -> {
      		System.out.println("###### Init Context Closed: " + ctx);
      	});
      	context.onDialog(dialog -> {
      		System.out.println("###### Init Dialog Load: " + dialog.message());
      		dialog.accept();
      	});
      	context.onRequest(req -> {
      		System.out.println("###### Init Request: " + req.url() + ":" + req + ":" + req.frame().page());
      	});
      	context.onResponse(res -> {
      		System.out.println("###### Init Response: " + res.url() + ":" + res + ":" + res.frame().page());
      		
      	});
      	
      	List<Page> pageList = context.pages();
      	System.out.println("@@@@PageList");
      	for(Page page : pageList) {
      		if(page != null) {
      			/*
      			 * 이동한 직후에는 WebSocket Client에 이벤트가 잡히지 않으나, 이동한 페이지에서 Event가 발생하면 페이지 이동 부터 이벤트가 잡힘.
      			 */
//      			System.out.println("@@@@Page Navigate http://211.109.22.33:8791/");
//      			page.navigate("http://211.109.22.33:8791/");
      		}
      	}
      }
      
      this.log.info("Browser connected");
		
	}
	
	public void stopRecording() throws InterruptedException, ExecutionException {
        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "none");

        JsonNode result = this.sendByEventSession("setRecorderMode", params).get();

        this.log.info("Recorder stopped: {}", result);
        
        this.close();
	}	
	
	private CompletableFuture<JsonNode> sendByEventSession(String method, JsonNode params) {
		long id = this.reqId.incrementAndGet();
		
		ObjectNode cmd = this.objectMapper.createObjectNode();
		cmd.put("id", id)
		   .put("guid", "DebugController") // Playwright VS Code extension의 BackendClient도 항상 guid를 DebugController로 지정한다.
		   .put("method", method)
		   .set("params", params);
		
		ObjectNode meta = this.objectMapper.createObjectNode();
		meta.put("timeout", 0);
		
		cmd.set("metadata", meta);
		
		CompletableFuture<JsonNode> future = new CompletableFuture<>();
		this.pendingReqs.put(id, future);
		
		try {
			String json = this.objectMapper.writeValueAsString(cmd);
			
			this.log.info("browser event request message: {}" + json);
			
			this.browserEventWsSession.sendMessage(new TextMessage(json));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return future;
	}

	private void close() {
		if(this.browserEventWsSession != null && this.browserEventWsSession.isOpen()) {
			try {
				this.browserEventWsSession.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	private class BrowserEventWebSocketHandler extends TextWebSocketHandler {
		private final RecordingWebSocketHandler handler;
		private final Logger log;
		private final ObjectMapper objectMapper;
		private final Map<Long, CompletableFuture<JsonNode>> pendingReqs;
		
		
		public BrowserEventWebSocketHandler(RecordingWebSocketHandler handler) {
			this.handler = handler;
			this.log = TestScriptRecordProcessor.this.log;
			this.objectMapper = TestScriptRecordProcessor.this.objectMapper;
			this.pendingReqs = TestScriptRecordProcessor.this.pendingReqs;
		}


		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			String payload = message.getPayload();
			
			this.log.info("browser event message: {}", payload);
			
			JsonNode root = this.objectMapper.readTree(payload);
			
			// id가 있으면 요청에 대한 응답
			if(root.has("id")) {
				long id = root.get("id").asLong();
				CompletableFuture<JsonNode> future = this.pendingReqs.remove(id);
				if(future == null)
					return;
				
				if(root.has("error")) {
					future.completeExceptionally(new RuntimeException(root.get("error").toPrettyString()));
				} else {
					future.complete(root.get("result"));
				}
				
				return;
			}
			
			// id가 없으면 server -> client event
			if(root.has("method")) {
				String method = root.get("method").asText();
				JsonNode params = root.get("params");
				this.handler.handleEvent(method, params);
			} else {
				this.log.warn("Unexpected message: {}", payload);
			}

		}
		
	}



}
