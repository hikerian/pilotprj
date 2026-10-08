package com.hikerian.autotest;

import java.io.BufferedReader;
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


public class PlaywrightDualClient {

    private final ObjectMapper mapper = new ObjectMapper();

    /*
     * DebugController connection
     */
    private WebSocketSession debugSession;

    /*
     * 일반 Playwright protocol connection
     */
    private WebSocketSession playwrightSession;

    /*
     * 두 protocol은 request id 공간을
     * 별도로 관리하는 것이 안전하다.
     */
    private final AtomicLong debugRequestId = new AtomicLong();

    private final AtomicLong playwrightRequestId = new AtomicLong();


    private final Map<Long, CompletableFuture<JsonNode>> debugPending = new ConcurrentHashMap<>();

    private final Map<Long, CompletableFuture<JsonNode>> playwrightPending = new ConcurrentHashMap<>();


    /*
     * Playwright protocol에서 생성되는
     * object guid를 저장한다.
     *
     * 예:
     *
     * browser-type@...
     * browser@...
     * browser-context@...
     * page@...
     */
    private final Map<String, RemoteObject> objects = new ConcurrentHashMap<>();

    private Process playwrightProcess;

    /*
     * initialize 결과의 Playwright guid
     */
    private String playwrightGuid;

    /*
     * preLaunched Browser guid
     */
    private String browserGuid;


    // ------------------------------------------------------
    // server
    // ------------------------------------------------------
    
    public void installChromium() throws Exception {
    	Driver driver = Driver.ensureDriverInstalled(Collections.emptyMap(), false);
    	ProcessBuilder builder = driver.createProcessBuilder();

    	String[] args = new String[]{"install",
    			"chromium"};
    	
    	builder.command().addAll(Arrays.asList(args));
    	
        builder.inheritIO();

        Process process = builder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                "Chromium installation failed: " + exitCode
            );
        }
    }

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

        builder.environment().put("PW_CODEGEN_NO_INSPECTOR", "1");
        builder.environment().put("PW_EXTENSION_MODE","1");

        builder.redirectErrorStream(true);

        this.playwrightProcess = builder.start();


        BufferedReader reader = new BufferedReader(new InputStreamReader(this.playwrightProcess.getInputStream()));

        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("[Playwright Server] " + line);

            if (line.startsWith("Listening on ")) {

                return line.substring("Listening on ".length()).trim();
            }
        }

        throw new IllegalStateException(
                "Playwright WebSocket endpoint를 찾지 못했습니다."
        );
    }


    // ------------------------------------------------------
    // DebugController connection
    // ------------------------------------------------------

    public void connectDebugController(String wsEndpoint) throws Exception {
        String endpoint = appendQuery(wsEndpoint, "debug-controller");

        StandardWebSocketClient client = new StandardWebSocketClient();

        this.debugSession = client.execute(new DebugHandler(), null, URI.create(endpoint)).get();

        System.out.println("[Debug] connected: " + endpoint);
    }


    public void initializeDebugController() throws Exception {
        ObjectNode params = this.mapper.createObjectNode();

        params.put("codegenId", "playwright-test");
        params.put("sdkLanguage", "java");

        this.debugCommand("initialize", params).get();

        ObjectNode state = this.mapper.createObjectNode();

        state.put("enabled", true);

        this.debugCommand("setReportStateChanged", state).get();
    }


    public void startRecording() throws Exception {
        ObjectNode params = this.mapper.createObjectNode();

        params.put("mode", "recording");
        params.put("testIdAttributeName", "data-testid");
        params.put("generateAutoExpect", true);

        this.debugCommand("setRecorderMode", params).get();
    }


    public void stopRecording() throws Exception {

        ObjectNode params = this.mapper.createObjectNode();

        params.put("mode", "none");

        this.debugCommand("setRecorderMode", params).get();
    }


    private CompletableFuture<JsonNode> debugCommand(String method, JsonNode params) throws Exception {
        long id = this.debugRequestId.incrementAndGet();

        ObjectNode command = this.mapper.createObjectNode();

        command.put("id", id);
        command.put("guid", "DebugController");
        command.put("method", method);
        command.set("params", params);
        command.putObject("metadata").put("timeout", 0);

        CompletableFuture<JsonNode> future = new CompletableFuture<>();

        this.debugPending.put(id, future);

        this.debugSession.sendMessage(new TextMessage(command.toString()));

        return future;
    }


    // ------------------------------------------------------
    // ?connect=first connection
    // ------------------------------------------------------

    public void connectPlaywrightProtocol(String wsEndpoint) throws Exception {
        /*
         * debug-controller가 실행한 chromium과
         * 동일한 Playwright server browser에 연결.
         */
        String endpoint = this.appendQuery(wsEndpoint, "connect=first&browser=chromium");

        StandardWebSocketClient client = new StandardWebSocketClient();

        this.playwrightSession = client.execute(new PlaywrightProtocolHandler(), null, URI.create(endpoint)).get();

        System.out.println("[Protocol] connected: " + endpoint);

        /*
         * 일반 Playwright protocol은 반드시
         * Root.initialize()부터 시작해야 한다.
         */
        this.initializePlaywrightProtocol();
    }

    private void initializePlaywrightProtocol() throws Exception {
        ObjectNode params = this.mapper.createObjectNode();

        params.put("sdkLanguage", "java");

        /*
         * Root object의 guid는 빈 문자열("")이다.
         *
         * 이것이 DebugController protocol과
         * 가장 큰 차이점이다.
         */
        JsonNode result = this.playwrightCommand("", "initialize", params).get();

        System.out.println("[Protocol initialize result]");
        System.out.println(result.toPrettyString());


        /*
         * 일반적으로:
         *
         * result.playwright.guid
         *
         * 형태로 Playwright object guid가 온다.
         */
        this.playwrightGuid = result.path("playwright").path("guid").asText(null);

        System.out.println("playwrightGuid = " + this.playwrightGuid);
    }


    private CompletableFuture<JsonNode> playwrightCommand(String guid, String method, JsonNode params) throws Exception {
        long id = this.playwrightRequestId.incrementAndGet();

        ObjectNode command = this.mapper.createObjectNode();

        command.put("id", id);
        command.put("guid", guid);
        command.put("method", method);
        command.set("params", params);

        ObjectNode metadata = command.putObject("metadata");

        metadata.put("timeout", 0);

        metadata.put("internal", false);

        CompletableFuture<JsonNode> future = new CompletableFuture<>();

        this.playwrightPending.put(id, future);

        System.out.println("[Protocol SEND] " + command);

        this.playwrightSession.sendMessage(new TextMessage(command.toString()));

        return future;
    }


    // ------------------------------------------------------
    // Debug WebSocket handler
    // ------------------------------------------------------

    private class DebugHandler extends TextWebSocketHandler {

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            JsonNode root = mapper.readTree(message.getPayload());

            /*
             * RPC response
             */
            if (root.has("id")) {
                long id = root.get("id").asLong();

                CompletableFuture<JsonNode> future = debugPending.remove(id);

                if (future == null)
                    return;

                if (root.has("error")) {
                    future.completeExceptionally(new RuntimeException(root.get("error").toPrettyString()));
                } else {
                    future.complete(root.path("result"));
                }

                return;
            }


            /*
             * server event
             */
            String method = root.path("method").asText();

            JsonNode params = root.path("params");


            handleDebugEvent(method, params);
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            System.out.println("[Debug] closed: " + status);
        }
    }


    // ------------------------------------------------------
    // Playwright protocol handler
    // ------------------------------------------------------

    private class PlaywrightProtocolHandler extends TextWebSocketHandler {

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

            JsonNode root = mapper.readTree(message.getPayload());

            System.out.println("[Protocol RECV] " + root);

            /*
             * command response
             */
            if (root.has("id")) {

                long id = root.get("id").asLong();

                CompletableFuture<JsonNode> future = playwrightPending.remove(id);

                if (future == null) return;

                if (root.has("error")) {
                    future.completeExceptionally(new RuntimeException(root.get("error").toPrettyString()));
                } else {
                    future.complete(root.path("result"));
                }

                return;
            }


            /*
             * 여기부터 Playwright wire protocol event
             */
            String guid = root.path("guid").asText();

            String method = root.path("method").asText();

            JsonNode params = root.path("params");

            switch (method) {

                /*
                 * Playwright remote object 생성
                 *
                 * 매우 중요.
                 */
                case "__create__" ->
                        handleCreate(guid, params);
                case "__dispose__" -> {
                    String disposedGuid = params.path("guid").asText(null);

                    if (disposedGuid != null)
                        objects.remove(disposedGuid);
                }
                case "__adopt__" -> {
                    System.out.println("[ADOPT] " + params);
                }
                default ->
                        handleProtocolEvent(guid, method, params);
            }
        }
    }


    // ------------------------------------------------------
    // Remote object registration
    // ------------------------------------------------------

    private void handleCreate(String parentGuid, JsonNode params) {
        String type = params.path("type").asText();

        String guid = params.path("guid").asText();

        JsonNode initializer = params.path("initializer");


        RemoteObject object = new RemoteObject(type, guid, parentGuid, initializer);

        objects.put(guid, object);

        System.out.println("[CREATE]" + " type=" + type + " guid=" + guid + " parent=" + parentGuid);


        /*
         * connect=first의 preLaunched Browser를 찾는다.
         */
        if ("Browser".equals(type)) {
            browserGuid = guid;

            System.out.println("Browser detected: " + browserGuid);
        }


        if ("BrowserContext".equals(type)) {
            System.out.println("BrowserContext detected: " + guid);
        }


        if ("Page".equals(type)) {
            System.out.println("Page detected: " + guid);

            System.out.println(initializer.toPrettyString());
        }
    }


    // ------------------------------------------------------
    // 일반 Playwright event
    // ------------------------------------------------------

    private void handleProtocolEvent(String guid, String method, JsonNode params) {
        RemoteObject object = objects.get(guid);

        String type = object != null ? object.type() : "unknown";

        System.out.println("[EVENT]" + " type=" + type + " guid=" + guid + " method=" + method);


        switch (type) {
            case "Page" -> {
                switch (method) {
                    case "frameNavigated" ->
                            System.out.println("Navigation: " + params);
                    case "console" ->
                            System.out.println("Console: " + params);
                    case "download" ->
                            System.out.println("Download: " + params);
                    case "dialog" -> System.out.println("Dialog: " + params);
                    case "webSocket" -> System.out.println("WebSocket: " + params);
                    default ->
                            System.out.println(params);
                }
            }


            case "BrowserContext" -> {
                switch (method) {
                    case "page" ->
                            System.out.println("New Page: " + params);
                    case "request" ->
                            System.out.println("Request: " + params);
                    case "response" ->
                            System.out.println("Response: " + params);
                    case "requestFinished" ->
                            System.out.println("Request finished: " + params);
                    case "requestFailed" ->
                            System.out.println("Request failed: " + params);
                    default ->
                            System.out.println(params);
                }
            }

            default ->
                    System.out.println(params);
        }
    }


    // ------------------------------------------------------
    // Recorder event
    // ------------------------------------------------------

    private void handleDebugEvent(String method, JsonNode params) {

        switch (method) {
            case "sourceChanged" -> {
                System.out.println("\n===== RECORDER =====");

                JsonNode actions = params.path("actions");

                if (actions.isArray()) {
                    for (JsonNode action : actions) {
                        System.out.println(action.asText());
                    }
                }
            }
            case "inspectRequested" -> {
                System.out.println("\n===== INSPECT =====");
                System.out.println("selector = " + params.path("selector").asText());
                System.out.println("locator = " + params.path("locator").asText());
                System.out.println("ariaSnapshot = " + params.path("ariaSnapshot"));
            }
            case "stateChanged" ->
                    System.out.println("[STATE] " + params);
            default ->
                    System.out.println("[DEBUG EVENT] " + method + " " + params);
        }
    }


    // ------------------------------------------------------
    // helper
    // ------------------------------------------------------

    private String appendQuery(String endpoint, String query) {

        if (endpoint.contains("?"))
            return endpoint + "&" + query;

        return endpoint + "?" + query;
    }


    // ------------------------------------------------------
    // close
    // ------------------------------------------------------

    public void close() {
        try {

            if (debugSession != null && debugSession.isOpen()) {
                debugSession.close();
            }
        } catch (Exception ignored) {
        	ignored.printStackTrace();
        }

        try {

            if (playwrightSession != null && playwrightSession.isOpen()) {
                playwrightSession.close();
            }
        } catch (Exception ignored) {
        	ignored.printStackTrace();
        }

        if (playwrightProcess != null)
            playwrightProcess.destroy();
    }


    // ------------------------------------------------------
    // Remote object DTO
    // ------------------------------------------------------

    record RemoteObject(String type, String guid, String parentGuid, JsonNode initializer) {
    }


    // ------------------------------------------------------
    // MAIN
    // ------------------------------------------------------

    public static void main(String[] args) throws Exception {
        PlaywrightDualClient client = new PlaywrightDualClient();

        try {
//        	client.installChromium();

            /*
             * 1. server
             */
            String endpoint = client.startPlaywrightServer();


            /*
             * 2. Recorder control connection
             */
            client.connectDebugController(endpoint);

            client.initializeDebugController();


            /*
             * 3. Recorder 시작
             *
             * 이 과정에서 Chromium/Page가 생성될 수 있다.
             */
            client.startRecording();


            /*
             * 4. 동일 server browser에
             *    connect=first 연결
             */
            client.connectPlaywrightProtocol(endpoint);


            System.out.println();
            System.out.println("======================================");
            System.out.println("Recorder + Playwright Protocol connected");
            System.out.println("Browser에서 동작을 수행하세요.");
            System.out.println("Enter를 누르면 종료합니다.");
            System.out.println("======================================");
            System.in.read();

            client.stopRecording();
        } finally {

            client.close();
        }
    }
}
