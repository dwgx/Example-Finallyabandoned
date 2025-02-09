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

    // WebSocket 服务器
    private static WebSocketServer wsServer;
    private static final Set<WebSocket> connectedClients = ConcurrentHashMap.newKeySet();
    private static boolean isWebSocketServerRunning = false;

    public static void startServer() throws IOException {
        // 启动 HTTP 服务器（端口 8080）
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/", new StaticFileHandler("gui"));
        httpServer.createContext("/modules", new ModulesHandler());
        httpServer.createContext("/modules/toggle", new ToggleModuleHandler());
        httpServer.createContext("/config/export", new ExportConfigHandler());
        httpServer.createContext("/config/import", new ImportConfigHandler());
        httpServer.start();
        System.out.println("HTTP ModuleServer started on port 8080");

        // 启动 WebSocket 服务器（端口 8081）
        wsServer = new WebSocketServer(new InetSocketAddress(8081)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                connectedClients.add(conn);
                System.out.println("New WS connection from " + conn.getRemoteSocketAddress());
                sendModulesStatus(conn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                connectedClients.remove(conn);
                System.out.println("WS closed: " + conn.getRemoteSocketAddress());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("Received WS message: " + message);
                Map<String, Object> msg = gson.fromJson(message, Map.class);
                String action = (String) msg.get("action");
                if (action == null) return;

                switch (action) {
                    case "getModules":
                        sendModulesStatus(conn);
                        break;
                    case "toggleModule":
                        handleToggleModule(msg);
                        break;
                    case "bindKey":
                        handleBindKey(msg);
                        break;
                    case "unbindKey":
                        handleUnbindKey(msg);
                        break;
                    case "setValue":
                        handleSetValue(msg);
                        break;
                    case "close":
                        shutdownServer();
                        break;
                    default:
                        System.out.println("Unknown action: " + action);
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
                if (conn != null && !conn.isClosed()) {
                    connectedClients.remove(conn);
                }
            }

            @Override
            public void onStart() {
                System.out.println("WebSocket Server started on port 8081");
                isWebSocketServerRunning = true;
            }
        };
        wsServer.start();
    }

    // 处理切换模块启用/禁用请求
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

    // 处理绑定快捷键请求
    private static void handleBindKey(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        Object key = msg.get("key");

        if (moduleName == null || key == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module != null) {
            int keyInt = -1;
            if (key instanceof String) {
                keyInt = KeyEvent.getExtendedKeyCodeForChar(((String) key).charAt(0));
            } else if (key instanceof Double) {
                keyInt = ((Double) key).intValue();
            }

            module.setShortcutKey(keyInt);
            broadcastModulesStatus();
        }
    }


    // 处理解绑快捷键请求
    private static void handleUnbindKey(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        if (moduleName == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module != null) {
            module.setShortcutKey(-1);
            broadcastModulesStatus();
        }
    }

    // 处理参数更新请求
    private static void handleSetValue(Map<String, Object> msg) {
        String moduleName = (String) msg.get("moduleName");
        String valueName = (String) msg.get("valueName");
        String newValueStr = (String) msg.get("newValue");
        if (moduleName == null || valueName == null || newValueStr == null) return;
        AbstractModule module = ModuleManager.getInstance().getModuleByName(moduleName);
        if (module == null) {
            System.out.println("Module not found: " + moduleName);
            return;
        }
        module.getValues().stream()
                .filter(v -> v.getName().equalsIgnoreCase(valueName))
                .findFirst()
                .ifPresent(val -> {
                    try {
                        // 如果是 RangeNumberValue 且字段为 "secondValue"，单独更新 secondValue
                        if ("secondValue".equalsIgnoreCase(valueName) && val instanceof com.example.value.RangeNumberValue<?> rangeVal) {
                            double d2 = Double.parseDouble(newValueStr);
                            Object oldSecond = rangeVal.getSecondValue();
                            if (oldSecond instanceof Integer) {
                                rangeVal.setSecondValue((int) d2);
                            } else if (oldSecond instanceof Float) {
                                rangeVal.setSecondValue((float) d2);
                            } else if (oldSecond instanceof Double) {
                                rangeVal.setSecondValue(d2);
                            } else {
                                throw new IllegalArgumentException("Unsupported secondValue type");
                            }
                        } else {
                            Object oldValue = val.getValue();
                            if (val instanceof com.example.value.ChoiceValue<?>.Multi) {
                                // 多选：假设以逗号分隔，且 T 为 String
                                String[] parts = newValueStr.split(",");
                                Collection<String> coll = new ArrayList<>();
                                for (String p : parts) {
                                    if (!p.trim().isEmpty()) {
                                        coll.add(p.trim());
                                    }
                                }
                                //noinspection unchecked
                                ((com.example.value.ChoiceValue<String>.Multi) val).setValue(coll);
                            } else if (val instanceof com.example.value.ChoiceValue<?>) {
                                // 单选
                                Object matched = ((com.example.value.ChoiceValue<?>) val).getOptions().stream()
                                        .filter(opt -> opt.toString().equalsIgnoreCase(newValueStr))
                                        .findFirst()
                                        .orElse(null);
                                if (matched == null) {
                                    throw new IllegalArgumentException("Invalid choice option: " + newValueStr);
                                }
                                //noinspection unchecked
                                ((com.example.value.ChoiceValue<Object>) val).setValue(matched);
                            } else if (val instanceof com.example.value.NumberValue<?>) {
                                double d = Double.parseDouble(newValueStr);
                                if (oldValue instanceof Integer) {
                                    //noinspection unchecked
                                    ((com.example.value.NumberValue<Integer>) val).setValue((int) d);
                                } else if (oldValue instanceof Long) {
                                    //noinspection unchecked
                                    ((com.example.value.NumberValue<Long>) val).setValue((long) d);
                                } else if (oldValue instanceof Float) {
                                    //noinspection unchecked
                                    ((com.example.value.NumberValue<Float>) val).setValue((float) d);
                                } else if (oldValue instanceof Double) {
                                    //noinspection unchecked
                                    ((com.example.value.NumberValue<Double>) val).setValue(d);
                                } else {
                                    throw new IllegalArgumentException("Unsupported number type: " + oldValue.getClass());
                                }
                            } else if (oldValue instanceof Boolean) {
                                boolean b = Boolean.parseBoolean(newValueStr);
                                //noinspection unchecked
                                ((com.example.value.BasicValue<Boolean>) val).setValue(b);
                            } else if (oldValue instanceof String) {
                                //noinspection unchecked
                                ((com.example.value.BasicValue<String>) val).setValue(newValueStr);
                            } else {
                                throw new IllegalArgumentException("Unsupported value type: " + oldValue.getClass());
                            }
                        }
                        System.out.println("Updated " + moduleName + "." + valueName + " => " + newValueStr);
                        broadcastModulesStatus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    // 将所有模块信息整理成 List<Map<String,Object>>
    private static List<Map<String, Object>> collectModulesData() {
        return ModuleManager.getInstance().getModules().stream()
                .map(m -> {
                    Map<String, Object> moduleMap = new HashMap<>();
                    moduleMap.put("name", m.getName());
                    moduleMap.put("description", m.getDescription());
                    moduleMap.put("category", m.getCategory().toString());
                    moduleMap.put("enabled", m.isEnabled());
                    moduleMap.put("keybind", m.getShortcutKey().getPrimaryKey());
                    List<Map<String, Object>> values = m.getValues().stream()
                            .map(val -> {
                                Map<String, Object> valMap = new HashMap<>();
                                valMap.put("name", val.getName());
                                valMap.put("description", val.getDescription());
                                Object v = val.getValue();
                                valMap.put("value", (v != null ? v.toString() : "null"));
                                if (val instanceof com.example.value.ChoiceValue<?>) {
                                    if (val instanceof com.example.value.ChoiceValue<?>.Multi) {
                                        valMap.put("type", "multiChoice");
                                    } else {
                                        valMap.put("type", "choice");
                                    }
                                    valMap.put("options", ((com.example.value.ChoiceValue<?>) val).getOptions()
                                            .stream().map(Object::toString).collect(Collectors.toList()));
                                } else if (val instanceof com.example.value.RangeNumberValue<?>) {
                                    valMap.put("type", "rangeNumber");
                                    com.example.value.RangeNumberValue<?> rn = (com.example.value.RangeNumberValue<?>) val;
                                    valMap.put("min", rn.getMin().toString());
                                    valMap.put("max", rn.getMax().toString());
                                    valMap.put("increment", rn.getIncrement().toString());
                                    valMap.put("secondValue", rn.getSecondValue().toString());
                                } else if (val instanceof com.example.value.NumberValue<?>) {
                                    valMap.put("type", "number");
                                    com.example.value.NumberValue<?> num = (com.example.value.NumberValue<?>) val;
                                    valMap.put("min", num.getMin().toString());
                                    valMap.put("max", num.getMax().toString());
                                    valMap.put("increment", num.getIncrement().toString());
                                } else if (v instanceof Boolean) {
                                    valMap.put("type", "boolean");
                                } else {
                                    valMap.put("type", "string");
                                }
                                return valMap;
                            })
                            .collect(Collectors.toList());
                    moduleMap.put("values", values);
                    return moduleMap;
                })
                .collect(Collectors.toList());
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
        System.out.println("Shutting down server...");
        try {
            if (wsServer != null && isWebSocketServerRunning) {
                wsServer.stop();
            }
            if (httpServer != null) {
                httpServer.stop(0);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********************************************************************
     * HTTP Handler 实现
     ********************************************************************/
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
                // 此处调用你的配置导出工具，将所有模块配置打包成 ZIP 文件
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
    // 示例配置导出/导入工具
    public static class MyConfigExportUtil {
        public static byte[] exportAllConfigsAsZip() {
            // 这里调用 ConfigUtils 与相关文件工具生成配置包（示例返回固定数据）
            String dummy = "这里是打包好的配置数据";
            return dummy.getBytes(StandardCharsets.UTF_8);
        }
    }
    public static class MyConfigImportUtil {
        public static void importConfigs(byte[] fileBytes) {
            System.out.println("Import config, bytes length: " + fileBytes.length);
            // 解析并设置配置，可调用 ConfigUtils.setValue(...) 更新配置
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
