package com.example.mod.server;

import com.example.mod.features.module.AbstractModule;
import com.example.mod.managers.ModuleManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModuleServer {
    private static HttpServer httpServer;
    private static final Gson gson = new Gson();
    private static WebSocketServer wsServer;
    private static final Set<WebSocket> connectedClients = ConcurrentHashMap.newKeySet();
    private static boolean isWebSocketServerRunning = false;

    public static void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        String guiPath = ModuleServer.class.getClassLoader().getResource("gui").getPath().substring(1);
        httpServer.createContext("/", new StaticFileHandler(guiPath));
        httpServer.createContext("/modules", new ModulesHandler());
        httpServer.createContext("/modules/toggle", new ToggleModuleHandler());
        httpServer.createContext("/config/export", new ExportConfigHandler());
        httpServer.createContext("/config/import", new ImportConfigHandler());
        httpServer.start();

        wsServer = new WebSocketServer(new InetSocketAddress(8081)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                connectedClients.add(conn);
                sendModulesStatus(conn);
            }
            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                connectedClients.remove(conn);
            }
            @Override
            public void onMessage(WebSocket conn, String message) {
                Map<String, Object> msg = gson.fromJson(message, Map.class);
                String action = (String) msg.get("action");
                if (action == null) return;
                switch (action) {
                    case "getModules" -> sendModulesStatus(conn);
                    case "toggleModule" -> handleToggleModule(msg);
                    case "bindKey" -> handleBindKey(msg);
                    case "unbindKey" -> handleUnbindKey(msg);
                    case "setValue" -> handleSetValue(msg);
                    case "close" -> shutdownServer();
                }
            }
            @Override
            public void onError(WebSocket conn, Exception ex) {
                if (conn != null && !conn.isClosed()) {
                    connectedClients.remove(conn);
                }
            }
            @Override
            public void onStart() {
                isWebSocketServerRunning = true;
                openBrowser();
            }
        };
        wsServer.start();
    }

    private static void openBrowser() {
        try {
            String url = "http://localhost:8080";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                // Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
                // Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                // Runtime.getRuntime().exec("xdg-open " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void handleToggleModule(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        Boolean enabled = (Boolean) msg.get("enabled");
        if (moduleName == null || enabled == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module != null) {
            module.setEnabled(enabled);
            broadcastModulesStatus();
        }
    }

    private static void handleBindKey(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        Object key = msg.get("key");
        if (moduleName == null || key == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module != null) {
            int keyInt = -1;
            if (key instanceof String s) {
                keyInt = KeyEvent.getExtendedKeyCodeForChar(s.charAt(0));
            } else if (key instanceof Double d) {
                keyInt = d.intValue();
            }
            module.setShortcutKey(keyInt);
            broadcastModulesStatus();
        }
    }

    private static void handleUnbindKey(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        if (moduleName == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module != null) {
            module.setShortcutKey(-1);
            broadcastModulesStatus();
        }
    }

    private static void handleSetValue(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        String valueName = (String) msg.get("valueName");
        String newValueStr = (String) msg.get("newValue");
        if (moduleName == null || valueName == null || newValueStr == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module == null) return;
        module.getValues().stream()
                .filter(v -> v.getName().equalsIgnoreCase(valueName))
                .findFirst()
                .ifPresent(val -> {
                    try {
                        Object oldValue = val.getValue();
                        if ("secondValue".equalsIgnoreCase(valueName) && val instanceof com.example.value.RangeNumberValue<?> rn) {
                            double d2 = Double.parseDouble(newValueStr);
                            Object oldSecond = rn.getSecondValue();
                            if (oldSecond instanceof Integer) rn.setSecondValue((int) d2);
                            else if (oldSecond instanceof Float) rn.setSecondValue((float) d2);
                            else if (oldSecond instanceof Double) rn.setSecondValue(d2);
                        } else if (val instanceof com.example.value.ChoiceValue<?>.Multi multi) {
                            String[] parts = newValueStr.split(",");
                            Collection<String> c = Arrays.stream(parts)
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList());
                            @SuppressWarnings("unchecked")
                            com.example.value.ChoiceValue<String>.Multi multiString =
                                    (com.example.value.ChoiceValue<String>.Multi) multi;
                            multiString.setValue(c);
                        } else if (val instanceof com.example.value.ChoiceValue<?> choice) {
                            Object matched = choice.getOptions().stream()
                                    .filter(opt -> opt.toString().equalsIgnoreCase(newValueStr))
                                    .findFirst()
                                    .orElse(null);
                            if (matched != null) {
                                @SuppressWarnings("unchecked")
                                com.example.value.ChoiceValue<Object> castChoice =
                                        (com.example.value.ChoiceValue<Object>) choice;
                                castChoice.setValue(matched);
                            }
                        } else if (val instanceof com.example.value.NumberValue<?> nv) {
                            double d = Double.parseDouble(newValueStr);
                            if (oldValue instanceof Integer) {
                                @SuppressWarnings("unchecked")
                                com.example.value.NumberValue<Integer> intNv =
                                        (com.example.value.NumberValue<Integer>) nv;
                                intNv.setValue((int) d);
                            } else if (oldValue instanceof Long) {
                                @SuppressWarnings("unchecked")
                                com.example.value.NumberValue<Long> longNv =
                                        (com.example.value.NumberValue<Long>) nv;
                                longNv.setValue((long) d);
                            } else if (oldValue instanceof Float) {
                                @SuppressWarnings("unchecked")
                                com.example.value.NumberValue<Float> floatNv =
                                        (com.example.value.NumberValue<Float>) nv;
                                floatNv.setValue((float) d);
                            } else if (oldValue instanceof Double) {
                                @SuppressWarnings("unchecked")
                                com.example.value.NumberValue<Double> doubleNv =
                                        (com.example.value.NumberValue<Double>) nv;
                                doubleNv.setValue(d);
                            }
                        } else if (oldValue instanceof Boolean) {
                            boolean b = Boolean.parseBoolean(newValueStr);
                            @SuppressWarnings("unchecked")
                            com.example.value.BasicValue<Boolean> boolVal =
                                    (com.example.value.BasicValue<Boolean>) val;
                            boolVal.setValue(b);
                        } else if (oldValue instanceof String) {
                            @SuppressWarnings("unchecked")
                            com.example.value.BasicValue<String> stringVal =
                                    (com.example.value.BasicValue<String>) val;
                            stringVal.setValue(newValueStr);
                        }
                        broadcastModulesStatus();
                    } catch (Exception ignored) {}
                });
    }

    private static List<Map<String, Object>> collectModulesData() {
        return ModuleManager.getInstance().getModules().stream().map(m -> {
            Map<String, Object> moduleMap = new HashMap<>();
            moduleMap.put("name", m.getName());
            moduleMap.put("description", m.getDescription());
            moduleMap.put("category", m.getCategory().toString());
            moduleMap.put("enabled", m.isEnabled());
            moduleMap.put("keybind", m.getShortcutKey().getPrimaryKey());
            List<Map<String, Object>> values = m.getValues().stream().map(val -> {
                Map<String, Object> valMap = new HashMap<>();
                valMap.put("name", val.getName());
                valMap.put("description", val.getDescription());
                Object v = val.getValue();
                valMap.put("value", (v != null ? v.toString() : "null"));
                if (val instanceof com.example.value.ChoiceValue<?> choice) {
                    if (val instanceof com.example.value.ChoiceValue<?>.Multi) valMap.put("type", "multiChoice");
                    else valMap.put("type", "choice");
                    valMap.put("options", choice.getOptions().stream().map(Object::toString).collect(Collectors.toList()));
                } else if (val instanceof com.example.value.RangeNumberValue<?> rn) {
                    valMap.put("type", "rangeNumber");
                    valMap.put("min", rn.getMin().toString());
                    valMap.put("max", rn.getMax().toString());
                    valMap.put("increment", rn.getIncrement().toString());
                    valMap.put("secondValue", rn.getSecondValue().toString());
                } else if (val instanceof com.example.value.NumberValue<?> num) {
                    valMap.put("type", "number");
                    valMap.put("min", num.getMin().toString());
                    valMap.put("max", num.getMax().toString());
                    valMap.put("increment", num.getIncrement().toString());
                } else if (v instanceof Boolean) {
                    valMap.put("type", "boolean");
                } else {
                    valMap.put("type", "string");
                }
                return valMap;
            }).collect(Collectors.toList());
            moduleMap.put("values", values);
            return moduleMap;
        }).collect(Collectors.toList());
    }

    private static void sendModulesStatus(WebSocket conn) {
        List<Map<String, Object>> modulesData = collectModulesData();
        Map<String, Object> response = new HashMap<>();
        response.put("action", "modulesStatus");
        response.put("modules", modulesData);
        conn.send(gson.toJson(response));
    }

    private static void broadcastModulesStatus() {
        List<Map<String, Object>> modulesData = collectModulesData();
        Map<String, Object> response = new HashMap<>();
        response.put("action", "modulesStatus");
        response.put("modules", modulesData);
        String json = gson.toJson(response);
        synchronized (connectedClients) {
            for (WebSocket client : connectedClients) {
                client.send(json);
            }
        }
    }

    private static void shutdownServer() {
        try {
            if (wsServer != null && isWebSocketServerRunning) {
                wsServer.stop();
            }
            if (httpServer != null) {
                httpServer.stop(0);
            }
            System.exit(0);
        } catch (Exception ignored) {}
    }

    static class StaticFileHandler implements HttpHandler {
        private final String basePath;
        public StaticFileHandler(String basePath) {
            this.basePath = basePath;
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String reqMethod = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(reqMethod) && !"HEAD".equalsIgnoreCase(reqMethod)) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            String reqPath = exchange.getRequestURI().getPath();
            if (reqPath.equals("/")) {
                reqPath = "/index.html";
            }
            String filePath = basePath + reqPath;
            if (!Files.exists(Paths.get(filePath))) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            String contentType = Files.probeContentType(Paths.get(filePath));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            if ("HEAD".equalsIgnoreCase(reqMethod)) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                exchange.sendResponseHeaders(200, fileBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }
            }
        }
    }

    static class ModulesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Map<String, Object>> modulesData = collectModulesData();
                String response = gson.toJson(modulesData);
                byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }
    }

    static class ToggleModuleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, Object> data = gson.fromJson(body, Map.class);
                    String moduleName = (String) data.get("moduleName");
                    Boolean enabled = (Boolean) data.get("enabled");
                    if (moduleName != null && enabled != null) {
                        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
                        if (module != null) {
                            module.setEnabled(enabled);
                            broadcastModulesStatus();
                        }
                    }
                }
                String response = "OK";
                byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }
    }

    static class ExportConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                byte[] configData = MyConfigExportUtil.exportAllConfigsAsZip();
                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"modules_config.zip\"");
                exchange.sendResponseHeaders(200, configData.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(configData);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }
    }

    static class ImportConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody()) {
                    byte[] fileBytes = is.readAllBytes();
                    MyConfigImportUtil.importConfigs(fileBytes);
                    broadcastModulesStatus();
                }
                String resp = "{\"status\":\"ok\"}";
                byte[] respBytes = resp.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }
    }

    private static void setCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    public static class MyConfigExportUtil {
        public static byte[] exportAllConfigsAsZip() {
            String dummy = "这里是打包好的配置数据";
            return dummy.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static class MyConfigImportUtil {
        public static void importConfigs(byte[] fileBytes) {
            // 导入配置逻辑（示例）
        }
    }

    public static void main(String[] args) {
        try {
            ModuleManager.getInstance().init();
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
