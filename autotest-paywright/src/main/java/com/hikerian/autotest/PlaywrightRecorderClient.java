package com.hikerian.autotest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.Driver;
import com.microsoft.playwright.options.AriaRole;


/**
 * vscode extension과 유사한 방식으로 playwright server와 연동하여 시나리오를 레코딩 하는 예시.
 */
public class PlaywrightRecorderClient {
	private final Logger log = LoggerFactory.getLogger(PlaywrightRecorderClient.class);
	
	
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicLong requestId = new AtomicLong();

    private final Map<Long, CompletableFuture<JsonNode>> pendingRequests = new ConcurrentHashMap<>();

    private WebSocketSession session;

    private Process playwrightProcess;
    
    /*
     * Browser 접근을 위한 Playwright 연결
     */
    private Playwright playwright;
    private Browser browser;

    
    public void installChromium() throws Exception {
    	Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
    	ProcessBuilder builder = driver.createProcessBuilder();
    	
    	this.log.info("ProcessBuilder Command: {}", builder.command());

    	String[] args = new String[]{"install", "chromium"};
    	
    	builder.command().addAll(Arrays.asList(args));
    	
//    	builder.directory(new File("C:/Temp/playwright"));
    	
        builder.inheritIO();

        Process process = builder.start();
        BufferedReader reader = process.inputReader(StandardCharsets.UTF_8);
        String txt = reader.readAllAsString();
        
        System.out.println("&&&&&&&&&&&&&&&&& INSTALL &&&&&&&&&&&&&&&&&::" + txt); // 아무 데이터도 없음.

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException("Chromium installation failed: " + exitCode);
        }
    }

    /**
     * Playwright run-server 실행
     *
     * @param projectDir Playwright 프로젝트 디렉터리
     * @param cliPath    node_modules/@playwright/test/cli.js 경로
     */
    public String startPlaywrightServer() throws Exception {
    	Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
    	ProcessBuilder builder = driver.createProcessBuilder();
    	
    	/*
    	 * --mode:
    	 * ㆍdefault: 일반 Playwright WebSocket 서버로 동작하며, 클라이언트 연결에 사용할 브라우저를 실행하고 원격 브라우저 자동화에 사용
    	 * ㆍextension: 브라우저 재사용 및 DebugController 연결을 지원하는 모드로 Recorder, Inspector, VS Code 연동시 사용
    	 */
    	String[] args = new String[]{"run-server",
    			"--host", "127.0.0.1",
                "--mode", "extension",
                "--path", "/java-recorder"};
    	
    	builder.command().addAll(Arrays.asList(args));

        /*
         * VS Code extension에서도 사용하는 환경변수.
         *
         * PW_CODEGEN_NO_INSPECTOR:
         *   별도 Inspector window를 띄우지 않는다.
         *
         * PW_EXTENSION_MODE:
         *   extension/debug-controller 형태로 동작하도록 한다.
         */
        builder.environment().put("PW_CODEGEN_NO_INSPECTOR","1");
        builder.environment().put("PW_EXTENSION_MODE","1");

        builder.redirectErrorStream(true);

        this.playwrightProcess = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(this.playwrightProcess.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {

        	this.log.info("[Playwright] {}", line);

            /*
             * Playwright run-server 출력 예:
             * Listening on ws://127.0.0.1:53219/java-recorder
             */
            if (line.startsWith("Listening on ")) {
            	String wsEndpoint = line.substring("Listening on ".length()).trim();
                this.log.info("WebSocket EndPoint: {}", wsEndpoint);
                
                return wsEndpoint;
            }
        }

        throw new IllegalStateException("Playwright WebSocket endpoint를 찾지 못했습니다.");
    }


    /**
     * Playwright DebugController WebSocket 연결
     */
    public void connect(String wsEndpoint) throws Exception {

        /*
         * VS Code extension의 핵심:
         * ws://.../java-recorder
         *                  ↓
         * ws://.../java-recorder?debug-controller
         */
        String debugEndpoint = wsEndpoint + "?debug-controller";

        this.log.info("Connecting: {}", debugEndpoint);

        StandardWebSocketClient client = new StandardWebSocketClient();

        CompletableFuture<WebSocketSession> future = client.execute(new PlaywrightWebSocketHandler(), null, URI.create(debugEndpoint));

        this.session = future.get();

        this.log.info("WebSocket connected.");
    }
    
    /**
     * Browser 접근을 위한 Playwright 연결
     * @param wsEndpoint
     * @throws Exception
     */
    public void connectBrowser(String wsEndpoint) throws Exception {
        this.playwright = Playwright.create();
//        this.browser = this.playwright.chromium().connect(wsEndpoint + "?connect=first&browser=chromium");
        this.browser = this.playwright.chromium().connect(wsEndpoint + "?connect=first");
//        this.browser = this.playwright.chromium().connect(wsEndpoint);

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
        			System.out.println("@@@@Page Navigate http://211.109.22.33:8791/");
        			page.navigate("http://211.109.22.33:8791/");
        		}
        	}
        }
        
        this.log.info("Browser connected");
    }


    /**
     * DebugController 초기화
     */
    public void initialize() throws Exception {
        ObjectNode params = this.objectMapper.createObjectNode();

        params.put("codegenId", "playwright-test");
        params.put("sdkLanguage", "java");

        JsonNode result = this.sendCommand("initialize", params).get();

        this.log.info("initialize completed: {}", result);

        /*
         * VS Code extension 역시 initialize 이후
         * setReportStateChanged(true)를 호출한다.
         */
        ObjectNode stateParams = this.objectMapper.createObjectNode();

        stateParams.put("enabled", true);

        this.sendCommand("setReportStateChanged", stateParams).get();
    }


    /**
     * Recording 시작
     */
    public void startRecording() throws Exception {

        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "recording");

        /*
         * 필요하다면:
         * params.put("testIdAttributeName", "data-testid");
         */
        this.sendCommand("setRecorderMode", params).get();

        this.log.info("Recorder started.");
    }


    /**
     * Recording 정지
     */
    public void stopRecording() throws Exception {

        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "none");

        this.sendCommand("setRecorderMode", params).get();

        this.log.info("Recorder stopped.");
    }


    /**
     * Playwright DebugController RPC 명령 전송
     */
    private CompletableFuture<JsonNode> sendCommand(String method, JsonNode params) throws Exception {

        long id = this.requestId.incrementAndGet();

        ObjectNode command = this.objectMapper.createObjectNode();
        command.put("id", id);

        /*
         * 매우 중요
         *
         * Playwright VS Code extension의 BackendClient도
         * 항상 guid를 DebugController로 지정한다.
         */
        command.put("guid", "DebugController");
        command.put("method", method);
        command.set("params", params);

        ObjectNode metadata = this.objectMapper.createObjectNode();

        metadata.put("timeout", 0);

        command.set("metadata", metadata);

        CompletableFuture<JsonNode> future = new CompletableFuture<>();

        this.pendingRequests.put(id, future);

        String json = this.objectMapper.writeValueAsString(command);

        this.log.info(">> WebSocket Request Message {}", json);

        this.session.sendMessage(new TextMessage(json));

        return future;
    }


    /**
     * WebSocket 메시지 처리
     */
    private class PlaywrightWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
        	PlaywrightRecorderClient.this.log.info("Playwright WebSocket session established.");
        }


        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

            String payload = message.getPayload();

            PlaywrightRecorderClient.this.log.info("<< {}", payload);

            JsonNode root = PlaywrightRecorderClient.this.objectMapper.readTree(payload);
            
            System.out.println("##Server WebSocket Message: " + root.toPrettyString());

            /*
             * id가 있으면 RPC response
             */
            if (root.has("id")) {
                long id = root.get("id").asLong();
                
            	System.out.println("##Server WebSocket Message ID:" + id);
                CompletableFuture<JsonNode> future = pendingRequests.remove(id);
                if (future == null) return;
                if (root.has("error")) {
                    future.completeExceptionally(new RuntimeException(root.get("error").toPrettyString()));
                } else {
                    future.complete(root.get("result"));
                }

                return;
            }


            /*
             * id가 없으면 server -> client event
             */
            if (root.has("method")) {
                String method = root.get("method").asText();
                JsonNode params = root.get("params");
                handleEvent(method, params);
            }
        }
        
    	@Override
    	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
    		System.out.println("*******************handleBinaryMessage*******************");
    	}


        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            exception.printStackTrace();
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        	PlaywrightRecorderClient.this.log.info("WebSocket closed: {}", status);
        }
    }


    /**
     * Playwright Recorder event 처리
     */
    private void handleEvent(String method, JsonNode params) {
    	this.log.info("-----------------------");
    	this.log.info("{} : {}", method, params);
    	this.log.info("-----------------------");
    	
    	
        switch (method) {
            /*
             * Recorder가 새로운 코드를 생성했을 때
             */
            case "sourceChanged" -> {
            	this.log.info("\n===== Recorded Source =====");

                if (params != null && params.has("actions")) {
                    for (JsonNode action : params.get("actions")) {
                    	this.log.info(action.asText());
                    	
                    	List<BrowserContext> contexts = this.browser.contexts();
                    	
                    	this.log.info("Browser Contexts: {}", contexts);
                    	
                    	for(BrowserContext context : contexts) {
                    		List<Page> pages = context.pages();
                    		for(Page page : pages) {
                    			if(page != null) {
                    				if(page.isClosed()) {
                    					this.log.info("Page url: {} Closed", page.url());
                    				} else {
                    					this.log.info("Page url: {}, title: {}", page.url(), page.title());
                    					
                            			Locator loc = page.getByRole(AriaRole.TEXTBOX, new GetByRoleOptions().setName("아이디"));
                            			this.log.info("==Searched Components: {}", loc == null ? "null" : loc.isVisible());
                    				}
                    			}
                    		}
                    	}
                    }
                }
            }
            /*
             * Locator picker에서 element를 선택
             */
            case "inspectRequested" -> {
            	this.log.info("\n===== Element Selected =====");

                if (params != null) {
                	this.log.info("locator = {}", params.path("locator").asText());
                	this.log.info("selector = {}", params.path("selector").asText());
                }
            }
            /*
             * browser/page 상태 변경
             */
            case "stateChanged" -> {
            	this.log.info("State changed: {}", params);
            }
            case "setModeRequested" -> {
            	this.log.info("Recorder mode: {}", params);
            }
            case "paused" -> {
            	this.log.info("Paused: {}", params);
            }
            default -> {
            	this.log.info("Event [{}]", params);
            }
        }
    }


    /**
     * 종료
     */
    public void close() {
        try {
            if (this.session != null && this.session.isOpen()) {
            	this.session.close();
            }
        } catch (Exception ignored) {
        	ignored.printStackTrace();
        }
        
        if(this.browser != null) {
        	this.browser.close();
        }
        if(this.playwright != null) {
        	this.playwright.close();
        }

        if (this.playwrightProcess != null) {
        	this.playwrightProcess.destroy();
        }
    }


    public static void main(String[] args) throws Exception {
        PlaywrightRecorderClient client = new PlaywrightRecorderClient();

        try {
        	client.installChromium();
        	
            /*
             * 1. Playwright run-server 실행
             */
            String wsEndpoint = client.startPlaywrightServer();

            /*
             * 2. ?debug-controller WebSocket 연결
             */
            client.connect(wsEndpoint);

            /*
             * 3. DebugController initialize
             */
            client.initialize();
            
            /*
             * 4. Recorder 시작
             */
            client.startRecording();
            
            /*
             * 5. Playwright Browser Connect
             */
            client.connectBrowser(wsEndpoint);
            

            System.out.println();
            System.out.println("================================");
            System.out.println("Browser에서 동작을 수행하세요.");
            System.out.println("Enter를 누르면 종료합니다.");
            System.out.println("================================");
            System.in.read();

            /*
             * 5. Recorder 종료
             */
            client.stopRecording();

        } finally {
            client.close();
        }
    }
}
