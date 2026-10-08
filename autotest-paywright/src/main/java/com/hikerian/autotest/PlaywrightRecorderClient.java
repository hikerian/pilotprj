package com.hikerian.autotest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.playwright.impl.driver.Driver;


/**
 * vscode extension과 유사한 방식으로 playwright server와 연동하여 시나리오를 레코딩 하는 예시.
 */
public class PlaywrightRecorderClient {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicLong requestId = new AtomicLong();

    private final Map<Long, CompletableFuture<JsonNode>> pendingRequests = new ConcurrentHashMap<>();

    private WebSocketSession session;

    private Process playwrightProcess;

    
    public void installChromium() throws Exception {
    	Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
    	ProcessBuilder builder = driver.createProcessBuilder();
    	
    	System.out.println(builder.command());

    	String[] args = new String[]{"install",
    			"chromium"};
    	
    	builder.command().addAll(Arrays.asList(args));
    	
//    	builder.directory(new File("C:/Temp/playwright"));
    	
        builder.inheritIO();

        Process process = builder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                "Chromium installation failed: " + exitCode
            );
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
    	
    	String[] args = new String[]{"run-server",
    			"--host",
                "127.0.0.1",
                "--mode",
                "extension",
                "--path",
                "/java-recorder"};
    	
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

            System.out.println("[Playwright] " + line);

            /*
             * Playwright run-server 출력 예:
             *
             * Listening on ws://127.0.0.1:53219/java-recorder
             */
            if (line.startsWith("Listening on ")) {

                return line.substring("Listening on ".length()).trim();
            }
        }

        throw new IllegalStateException("Playwright WebSocket endpoint를 찾지 못했습니다.");
    }


    /**
     * Playwright DebugController WebSocket 연결
     */
    public void connect(String wsEndpoint)
            throws Exception {

        /*
         * VS Code extension의 핵심:
         *
         * ws://.../java-recorder
         *
         *                  ↓
         *
         * ws://.../java-recorder?debug-controller
         */
        String debugEndpoint = wsEndpoint + "?debug-controller";

        System.out.println("Connecting: " + debugEndpoint);

        StandardWebSocketClient client = new StandardWebSocketClient();

        CompletableFuture<WebSocketSession> future = client.execute(new PlaywrightWebSocketHandler(), null, URI.create(debugEndpoint));

        this.session = future.get();

        System.out.println("WebSocket connected.");
    }


    /**
     * DebugController 초기화
     */
    public void initialize() throws Exception {
        ObjectNode params = this.objectMapper.createObjectNode();

        params.put("codegenId", "playwright-test");
        params.put("sdkLanguage", "java");

        JsonNode result = this.sendCommand("initialize", params).get();

        System.out.println("initialize completed: " + result);

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
         *
         * params.put(
         *     "testIdAttributeName",
         *     "data-testid"
         * );
         */

        this.sendCommand("setRecorderMode", params).get();

        System.out.println("Recorder started.");
    }


    /**
     * Recording 정지
     */
    public void stopRecording() throws Exception {

        ObjectNode params = this.objectMapper.createObjectNode();
        params.put("mode", "none");

        this.sendCommand("setRecorderMode", params).get();

        System.out.println("Recorder stopped.");
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

        System.out.println(">> " + json);

        this.session.sendMessage(new TextMessage(json));

        return future;
    }


    /**
     * WebSocket 메시지 처리
     */
    private class PlaywrightWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            System.out.println("Playwright WebSocket session established.");
        }


        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

            String payload = message.getPayload();

            System.out.println("<< " + payload);

            JsonNode root = objectMapper.readTree(payload);

            /*
             * id가 있으면 RPC response
             */
            if (root.has("id")) {

                long id = root.get("id").asLong();

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
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            exception.printStackTrace();
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            System.out.println("WebSocket closed: " + status);
        }
    }


    /**
     * Playwright Recorder event 처리
     */
    private void handleEvent(String method, JsonNode params) {
    	System.out.println("-----------------------");
    	System.out.println(method + ":" + params);
    	System.out.println("-----------------------");
    	
    	
        switch (method) {
            /*
             * Recorder가 새로운 코드를 생성했을 때
             */
            case "sourceChanged" -> {

                System.out.println("\n===== Recorded Source =====");

                if (params != null && params.has("actions")) {

                    for (JsonNode action : params.get("actions")) {
                        System.out.println(action.asText());
                    }
                }
            }
            /*
             * Locator picker에서 element를 선택
             */
            case "inspectRequested" -> {
                System.out.println("\n===== Element Selected =====");

                if (params != null) {
                    System.out.println("locator = " + params.path("locator").asText());

                    System.out.println("selector = " + params.path("selector").asText());
                }
            }
            /*
             * browser/page 상태 변경
             */
            case "stateChanged" -> {
                System.out.println("State changed: " + params);
            }
            case "setModeRequested" -> {
                System.out.println("Recorder mode: " + params);
            }
            case "paused" -> {
                System.out.println("Paused: " + params);
            }
            default -> {
                System.out.println("Event [" + method + "] " + params);
            }
        }
    }


    /**
     * 종료
     */
    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception ignored) {
        	ignored.printStackTrace();
        }

        if (playwrightProcess != null) {
            playwrightProcess.destroy();
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
            System.out.println("WebSocket EndPoint: " + wsEndpoint);

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
