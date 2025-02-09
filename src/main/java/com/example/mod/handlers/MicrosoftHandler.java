package com.example.mod.handlers;

import com.example.mod.utils.account.SessionUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepCredentialsMsaCode;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * MicrosoftHandler – 账户管理示例（高级UI动画 + 自动切换离线账号）
 *
 * 特点:
 * 1. 删除当前登录账号时，会自动登录离线账号，避免空 Session。
 * 2. 登录按钮有更精细的过渡动画：点击后先转圈，成功后打勾并变绿色。
 * 3. 离线账号不可删除，若尝试删除则提示错误信息。
 * 4. 保持来回切换账号时的按钮状态正常，不需要刷新页面。
 */
public class MicrosoftHandler {

    private static final int PORT = 1337;
    private static final File ACCOUNT_DATA_FILE = new File("accounts_data.txt");

    private HttpServer server;
    private final List<Account> accounts = new ArrayList<>();
    private Account currentAccount = null;

    public void init() {
        try {
            // 加载现有账号
            if (ACCOUNT_DATA_FILE.exists()) {
                loadAccounts();
            }

            // 检查是否已有离线账号
            boolean hasOffline = false;
            for (Account acc : accounts) {
                if ("offline".equals(acc.getType())) {
                    hasOffline = true;
                    if (currentAccount == null) {
                        currentAccount = acc;
                    }
                    break;
                }
            }
            // 若没有离线账号，也没有任何账号 => 自动新建一个离线账号
            if (!hasOffline && accounts.isEmpty()) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Account offlineAcc = new Account("离线玩家", UUID.randomUUID().toString(), "", now);
                accounts.add(offlineAcc);
                currentAccount = offlineAcc;
                saveAccounts();
            }

            startHttpServer();
            openBrowser();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (server != null) {
            server.stop(0);
            System.out.println("Local server stopped.");
        }
    }

    /**
     * 从文件加载账号列表
     */
    private void loadAccounts() {
        accounts.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_DATA_FILE))) {
            // 第一行可忽略
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    accounts.add(new Account(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存账号列表
     */
    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_DATA_FILE))) {
            writer.write("");
            writer.newLine();
            for (Account acc : accounts) {
                writer.write(acc.getUsername() + "|" + acc.getUserId() + "|" +
                        acc.getAccessToken() + "|" + acc.getAddedTime());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
        // "/" 重定向到 "/manage"
        server.createContext("/", ex -> {
            ex.getResponseHeaders().set("Location", "/manage");
            ex.sendResponseHeaders(302, -1);
        });
        server.createContext("/manage", new ManageHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("HTTP server started on port " + PORT);
    }

    private void openBrowser() {
        try {
            String url = "http://localhost:" + PORT + "/manage";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Please open manually: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open browser. Go to: http://localhost:" + PORT + "/manage");
        }
    }

    /**
     * 处理 /manage 请求
     */
    private class ManageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                String page = buildManagePage(accounts, null);
                sendResponse(exchange, page, 200);
            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> params = parseForm(exchange.getRequestBody());
                String action = params.get("action");
                if (action == null) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                switch (action) {
                    case "add"    -> handleAdd(params, exchange);
                    case "login"  -> handleLogin(params, exchange);
                    case "edit"   -> handleEdit(params, exchange);
                    case "delete" -> handleDelete(params, exchange);
                    default       -> exchange.sendResponseHeaders(400, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }

        // ------------------ 核心 CRUD 逻辑 ------------------ //

        /**
         * 添加账号
         */
        private void handleAdd(Map<String, String> params, HttpExchange exchange) throws IOException {
            String type = params.get("type");
            String username = params.get("username");
            String password = params.get("password");
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            if (type == null) type = "offline";

            if ("official".equalsIgnoreCase(type)) {
                // 正版账号
                if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    HttpClient httpClient = MinecraftAuth.createHttpClient();
                    StepCredentialsMsaCode.MsaCredentials creds =
                            new StepCredentialsMsaCode.MsaCredentials(username, password);
                    StepFullJavaSession.FullJavaSession session =
                            MinecraftAuth.JAVA_CREDENTIALS_LOGIN.getFromInput(httpClient, creds);
                    StepMCProfile.MCProfile profile = session.getMcProfile();
                    Account newAcc = new Account(
                            profile.getName(),
                            profile.getId().toString(),
                            profile.getMcToken().getAccessToken(),
                            now
                    );
                    accounts.add(newAcc);
                } catch (Exception e) {
                    e.printStackTrace();
                    String errPage = buildManagePage(accounts, "添加正版账户失败: " + e.getMessage());
                    sendResponse(exchange, errPage, 500);
                    return;
                }
            } else {
                // 离线账号
                if (username == null || username.isEmpty()) {
                    username = "离线玩家";
                }
                try { SessionUtils.logout(); } catch (Exception ignored) {}
                SessionUtils.update(SessionUtils.offline(username));

                Account newAcc = new Account(username, UUID.randomUUID().toString(), "", now);
                accounts.add(newAcc);
            }
            saveAccounts();
            redirect(exchange);
        }

        /**
         * 登录
         */
        private void handleLogin(Map<String, String> params, HttpExchange exchange) throws IOException {
            String userId = params.get("userId");
            if (userId == null || userId.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            boolean found = false;
            for (Account acc : accounts) {
                if (acc.getUserId().equals(userId)) {
                    found = true;
                    // 先登出
                    try { SessionUtils.logout(); } catch (Exception ignored) {}
                    // 再登录
                    try {
                        SessionUtils.update(SessionUtils.microsoft(
                                acc.getUsername(), acc.getUserId(), acc.getAccessToken()
                        ));
                        currentAccount = acc;
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errPage = buildManagePage(accounts, "登录失败: " + e.getMessage());
                        sendResponse(exchange, errPage, 500);
                        return;
                    }
                    break;
                }
            }
            if (!found) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            saveAccounts();
            exchange.sendResponseHeaders(200, -1);
        }

        /**
         * 编辑账号
         */
        private void handleEdit(Map<String, String> params, HttpExchange exchange) throws IOException {
            String userId = params.get("userId");
            if (userId == null || userId.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            String newUsername = params.get("newUsername");
            String newAccessToken = params.get("newAccessToken");

            for (Account acc : accounts) {
                if (acc.getUserId().equals(userId)) {
                    if (newUsername != null && !newUsername.isEmpty()) {
                        acc.setUsername(newUsername);
                    }
                    if (newAccessToken != null && !newAccessToken.isEmpty()) {
                        acc.setAccessToken(newAccessToken);
                    }
                    break;
                }
            }
            saveAccounts();
            redirect(exchange);
        }

        /**
         * 删除账号
         * - 离线账号不可删
         * - 若删除的是当前登录账号 => 自动切换到离线账号后再删除
         */
        private void handleDelete(Map<String, String> params, HttpExchange exchange) throws IOException {
            String userId = params.get("userId");
            if (userId == null || userId.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            Account toRemove = null;
            for (Account acc : accounts) {
                if (acc.getUserId().equals(userId)) {
                    toRemove = acc;
                    break;
                }
            }
            if (toRemove == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            if ("offline".equals(toRemove.getType())) {
                // 离线账号不可删除
                String errPage = buildManagePage(accounts, "离线账号无法删除。");
                sendResponse(exchange, errPage, 400);
                return;
            }

            // 如果删除的正是当前登录账号 => 切换到离线账号
            if (currentAccount != null && currentAccount.getUserId().equals(userId)) {
                // 找到离线账号
                for (Account acc : accounts) {
                    if ("offline".equals(acc.getType())) {
                        try { SessionUtils.logout(); } catch (Exception ignored) {}
                        // 更新离线 session
                        SessionUtils.update(SessionUtils.offline(acc.getUsername()));
                        currentAccount = acc;
                        break;
                    }
                }
            }

            // 真正删除
            accounts.remove(toRemove);
            saveAccounts();
            exchange.sendResponseHeaders(200, -1);
        }

        // ------------------ 工具 & 页面逻辑 ------------------ //

        private Map<String, String> parseForm(InputStream in) throws IOException {
            Map<String, String> result = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
            String[] pairs = sb.toString().split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    result.put(decodeUrl(kv[0]), decodeUrl(kv[1]));
                }
            }
            return result;
        }

        private String decodeUrl(String s) throws UnsupportedEncodingException {
            return java.net.URLDecoder.decode(s, "UTF-8");
        }

        private void sendResponse(HttpExchange exchange, String resp, int code) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            byte[] bytes = resp.getBytes("UTF-8");
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void redirect(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Location", "/manage");
            exchange.sendResponseHeaders(302, -1);
        }

        /**
         * 生成前端所需的 JSON
         */
        private String accountsToJson() {
            StringBuilder json = new StringBuilder("{");
            for (Account acc : accounts) {
                json.append("\"").append(acc.getUserId()).append("\":{")
                        .append("\"username\":\"").append(acc.getUsername()).append("\",")
                        .append("\"uuid\":\"").append(acc.getUserId()).append("\",")
                        .append("\"addedTime\":\"").append(acc.getAddedTime()).append("\",")
                        .append("\"type\":\"").append(acc.getType()).append("\"},");
            }
            if (json.length() > 1 && json.charAt(json.length()-1) == ',') {
                json.deleteCharAt(json.length()-1);
            }
            json.append("}");
            return json.toString();
        }

        /**
         * 生成管理页面（包含高级动画 & 错误消息）
         */
        private String buildManagePage(List<Account> accounts, String msg) {
            // 离线账号置顶
            accounts.sort((a, b) -> {
                if ("offline".equals(a.getType()) && !"offline".equals(b.getType())) return -1;
                if (!"offline".equals(a.getType()) && "offline".equals(b.getType())) return 1;
                return 0;
            });

            StringBuilder accountListHtml = new StringBuilder();
            for (Account acc : accounts) {
                boolean isCurrent = (currentAccount != null && currentAccount.getUserId().equals(acc.getUserId()));
                // 左侧渲染“头颅”
                accountListHtml.append("<div id='account-").append(acc.getUserId()).append("' class='account-item' onclick=\"selectAccount('")
                        .append(acc.getUserId()).append("')\">")
                        .append("<img class='avatar' src='").append(acc.getHeadUrl())
                        .append("' alt='head'>")
                        .append("<div class='account-info'>")
                        .append("<h3>").append(acc.getUsername()).append("</h3>")
                        .append("<p>").append("official".equals(acc.getType()) ? "正版" : "离线").append("</p>")
                        .append("</div>")
                        .append("<div class='btn-group'>");
                // 登录按钮
                if (isCurrent) {
                    accountListHtml.append("<button class='btn-action login-btn logged' data-uuid='").append(acc.getUserId())
                            .append("' disabled>已登录</button>");
                } else {
                    accountListHtml.append("<button class='btn-action login-btn' data-uuid='").append(acc.getUserId())
                            .append("' onclick=\"loginAccount('").append(acc.getUserId())
                            .append("', this); event.stopPropagation();\">登录</button>");
                }
                // 编辑、删除
                accountListHtml.append("<button class='btn-action edit-btn' onclick=\"openEditModal('")
                        .append(acc.getUserId()).append("'); event.stopPropagation();\">编辑</button>")
                        .append("<button class='btn-action delete-btn' onclick=\"openDeleteModal('")
                        .append(acc.getUserId()).append("'); event.stopPropagation();\">删除</button>")
                        .append("</div></div>");
            }

            // 若有错误消息 msg，不妨插入到页面
            String errorBar = "";
            if (msg != null && !msg.isEmpty()) {
                errorBar = "<div style='color: #f66; margin-bottom: 10px; font-weight: bold;'>" + msg + "</div>";
            }

            String accountsJson = accountsToJson();
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n")
              .append("<html lang='zh'>\n")
              .append("<head>\n")
              .append("  <meta charset='UTF-8'>\n")
              .append("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n")
              .append("  <title>账户管理</title>\n")
              // 引入图标 & 字体
              .append("  <link href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css' rel='stylesheet'>\n")
              .append("  <link href='https://fonts.googleapis.com/css2?family=Quicksand:wght@300;400;500;700&display=swap' rel='stylesheet'>\n")
              .append("  <style>\n")
              // 全局
              .append("    body { background: #1e1e1e; color: #e0e0e0; font-family: 'Quicksand', sans-serif; margin: 0; padding: 0;}\n")
              .append("    .container { display: flex; height: 100vh; }\n")
              .append("    .sidebar { width: 300px; background: #2a2a2e; padding: 16px; overflow-y: auto; }\n")
              .append("    .sidebar h2 { text-align: center; font-size: 24px; margin-bottom: 16px; }\n")
              .append("    .add-btn { width: 100%; padding: 10px; background: #007aff; border: none; border-radius: 4px; color: #fff; margin-bottom: 16px; cursor: pointer; transition: background 0.3s, transform 0.2s; }\n")
              .append("    .add-btn:hover { background: #005bb5; transform: scale(1.02); }\n")
              // 左侧列表
              .append("    .account-item { display: flex; align-items: center; background: #2a2a2e; padding: 10px; margin-bottom: 10px; border-radius: 4px; cursor: pointer; transition: transform 0.2s, background 0.2s;}\n")
              .append("    .account-item:hover { transform: scale(1.02); background: #323233;}\n")
              .append("    .avatar { width: 48px; height: 48px; border-radius: 6px; margin-right: 10px;}\n")
              .append("    .account-info { flex: 1;}\n")
              .append("    .account-info h3 { margin: 0; font-size: 16px;}\n")
              .append("    .account-info p { margin: 4px 0 0; font-size: 12px; color: #aaa;}\n")
              .append("    .btn-group { display: flex; gap: 6px;}\n")
              // 按钮动画
              .append("    .btn-action { background: #007aff; border: none; border-radius: 4px; padding: 6px 8px; color: #fff; cursor: pointer; font-size: 14px; transition: background 0.3s, transform 0.2s; }\n")
              .append("    .btn-action:hover { background: #005bb5; transform: scale(1.03);}\n")
              .append("    .btn-action:active { transform: scale(0.97);}\n")
              .append("    .btn-action:disabled { background: #555; cursor: default;}\n")
              // 登录按钮进阶动画
.append("    .login-btn.loading i {\n")
.append("      animation: spin 1s linear infinite;\n")  // 增加旋转时间至 2s
.append("    }\n")
.append("    .login-btn.success i {\n")
.append("      color: #28a745;\n")  // **绿色打勾**
.append("      animation: fadeIn 0.3s ease;\n")
.append("    }\n")
.append("    .login-btn.success {\n")
.append("      background-color: #28a745 !important;\n")  // **变绿**
.append("    }\n")
.append("    .login-btn.logged {\n")
.append("      background-color: #28a745;\n")  // **最终状态：已登录**
.append("      transition: background 0.4s ease;\n")  // **动画平滑**
.append("    }\n")
.append("    @keyframes spin {\n")
.append("      0% { transform: rotate(0deg); }\n")
.append("      100% { transform: rotate(360deg); }\n")
.append("    }\n")
.append("    @keyframes fadeIn {\n")
.append("      from { opacity: 0; }\n")
.append("      to { opacity: 1; }\n")
.append("    }\n")

              // 登录成功
              .append("    .login-btn.success {\n")
              .append("      background: #28a745 !important;\n")
              .append("      color: #fff !important;\n")
              .append("      position: relative;\n")
              .append("    }\n")
              .append("    .login-btn.success::after {\n")
              .append("      content: '\\2713'; /* 也可用 \\f00c from icons */\n")
              .append("      position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%) scale(0);\n")
              .append("      font-size: 16px;\n")
              .append("      animation: checkAppear 0.3s forwards;\n")
              .append("    }\n")
              .append("    @keyframes checkAppear {\n")
              .append("      0%   { transform: translate(-50%, -50%) scale(0); opacity: 0;}\n")
              .append("      100% { transform: translate(-50%, -50%) scale(1); opacity: 1;}\n")
              .append("    }\n")
              // 右侧详情
              .append("    .main-area { flex: 1; background: #1f1f1f; padding: 20px; overflow-y: auto; }\n")
              .append("    .detail-card { background: #272727; border-radius: 8px; padding: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.5); transition: opacity 0.5s, transform 0.5s; font-size: 18px;}\n")
              .append("    .detail-card h2 { margin-bottom: 12px;}\n")
              .append("    .detail-card p { margin: 6px 0;}\n")
              .append("    .detail-images { display: flex; gap: 20px; margin-top: 12px; justify-content: center; align-items: center;}\n")
              .append("    .detail-images img { border: 2px solid #444; border-radius: 6px; opacity: 0; animation: fadeIn 0.5s forwards;}\n")
              .append("    #detailHead { width: 64px; height: 64px;}\n")
              .append("    #detailSkin { width: 64px; height: 64px;}\n")
              .append("    #detailCape { width: 64px; height: 64px;}\n")
              .append("    #detailBody { width: 180px; height: 180px;}\n")
              .append("    @keyframes fadeIn { from { opacity: 0; transform: scale(0.95);} to { opacity: 1; transform: scale(1);} }\n")
              // 模态框
              .append("    .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); align-items: center; justify-content: center; z-index: 1000;}\n")
              .append("    .modal-content { background: #2a2a2a; padding: 20px; border-radius: 8px; width: 320px; box-shadow: 0 4px 12px rgba(0,0,0,0.8); position: relative; text-align: center; opacity: 0; transform: scale(0.95); transition: opacity 0.3s ease, transform 0.3s ease;}\n")
              .append("    .modal.show .modal-content { opacity: 1; transform: scale(1);}\n")
              .append("    .modal-content h2 { margin-bottom: 16px;}\n")
              .append("    .modal-content input, .modal-content select { width: 90%; padding: 8px; margin: 8px auto; display: block; border: 1px solid #444; border-radius: 4px; background: #1e1e1e; color: #fff;}\n")
              .append("    .modal-content button { width: 90%; padding: 10px; background: #007aff; border: none; border-radius: 4px; color: #fff; cursor: pointer; transition: background 0.3s; margin: 10px auto; display: block;}\n")
              .append("    .modal-content button:hover { background: #005bb5;}\n")
              .append("    .close-modal { position: absolute; top: 10px; right: 14px; font-size: 24px; cursor: pointer; color: #aaa;}\n")
              .append("    .modal-buttons { display: flex; gap: 10px; margin-top: 16px;}\n")
              .append("    .cancel-btn { background: #6c757d; flex: 1;}\n")
              .append("    .confirm-btn { background: #dc3545; flex: 1;}\n")
              .append("  </style>\n")
              .append("</head>\n")
              .append("<body>\n")
              .append("<div class='container'>\n")
              // 左侧
              .append("  <div class='sidebar'>\n")
              .append("    <h2>账户管理</h2>\n")
              .append(errorBar) // 如果有错误信息，就在此处显示
              .append("    <button class='add-btn' onclick='openAddModal()'>添加账户</button>\n")
              .append("    <div id='accountList'>\n")
              .append(accountListHtml.toString())
              .append("    </div>\n")
              .append("  </div>\n")
              // 右侧详情
              .append("  <div class='main-area'>\n")
              .append("    <div class='detail-card' id='detailCard'>\n")
              .append("      <h2>账户详情</h2>\n")
              .append("      <p id='detailUUID'>UUID: -</p>\n")
              .append("      <p id='detailUsername'>用户名: -</p>\n")
              .append("      <p id='detailTime'>添加时间: -</p>\n")
              .append("      <div class='detail-images'>\n")
              // 显示：头像 + 皮肤 + 披风 + 全身
              .append("        <img id='detailHead'  src='https://crafatar.com/avatars/00000000?size=64&default=MHF_Steve&overlay' alt='头像'>\n")
              .append("        <img id='detailSkin'  src='https://crafatar.com/skins/00000000?size=64&default=MHF_Steve'  alt='皮肤'>\n")
              .append("        <img id='detailCape'  src='https://crafatar.com/capes/00000000'                 alt='披风' onerror=\"this.style.display='none';\">\n")
              .append("        <img id='detailBody'  src='https://crafatar.com/renders/body/00000000?size=180&default=MHF_Steve&overlay' alt='全身'>\n")
              .append("      </div>\n")
              .append("    </div>\n")
              .append("  </div>\n")
              .append("</div>\n")

              // 模态框 - 添加
              .append("<div class='modal' id='addModal'>\n")
              .append("  <div class='modal-content'>\n")
              .append("    <span class='close-modal' onclick='closeAddModal()'>×</span>\n")
              .append("    <h2>添加账户</h2>\n")
              .append("    <select id='addType'>\n")
              .append("      <option value='official'>正版账户</option>\n")
              .append("      <option value='offline'>离线账户</option>\n")
              .append("    </select>\n")
              .append("    <input type='text' id='addUsername' placeholder='邮箱/用户名'>\n")
              .append("    <input type='password' id='addPassword' placeholder='密码(仅正版)'>\n")
              .append("    <button onclick='submitAdd()'>确定</button>\n")
              .append("  </div>\n")
              .append("</div>\n")

              // 模态框 - 编辑
              .append("<div class='modal' id='editModal'>\n")
              .append("  <div class='modal-content'>\n")
              .append("    <span class='close-modal' onclick='closeEditModal()'>×</span>\n")
              .append("    <h2>编辑账户</h2>\n")
              .append("    <input type='hidden' id='editUserId'>\n")
              .append("    <input type='text' id='editUsername' placeholder='新用户名'>\n")
              .append("    <input type='text' id='editAccessToken' placeholder='AccessToken(可选)'>\n")
              .append("    <button onclick='submitEdit()'>保存</button>\n")
              .append("  </div>\n")
              .append("</div>\n")

              // 模态框 - 删除
              .append("<div class='modal' id='deleteModal'>\n")
              .append("  <div class='modal-content'>\n")
              .append("    <span class='close-modal' onclick='closeDeleteModal()'>×</span>\n")
              .append("    <h2>确认删除</h2>\n")
              .append("    <p>确定要删除此账号吗？（离线账号不可删除）</p>\n")
              .append("    <div class='modal-buttons'>\n")
              .append("      <button class='btn-action cancel-btn' onclick='closeDeleteModal()'>取消</button>\n")
              .append("      <button class='btn-action confirm-btn' onclick='confirmDelete()'>确认</button>\n")
              .append("    </div>\n")
              .append("  </div>\n")
              .append("</div>\n")

              // JS
              .append("<script>\n")
              .append("var pendingDeleteAccountId = null;\n")
              .append("var accountsData = ").append(accountsJson).append(";\n")

              // 切换详情
              .append("function selectAccount(uuid){\n")
              .append("  var acc = accountsData[uuid];\n")
              .append("  if(!acc) return;\n")
              .append("  document.getElementById('detailUUID').innerText = 'UUID: ' + acc.uuid;\n")
              .append("  document.getElementById('detailUsername').innerText = '用户名: ' + acc.username;\n")
              .append("  document.getElementById('detailTime').innerText = '添加时间: ' + acc.addedTime;\n")
              .append("  document.getElementById('detailHead').src = 'https://crafatar.com/avatars/' + acc.uuid + '?size=64&default=MHF_Steve&overlay';\n")
              .append("  document.getElementById('detailSkin').src = 'https://crafatar.com/skins/' + acc.uuid + '?size=64&default=MHF_Steve';\n")
              .append("  var cape = document.getElementById('detailCape');\n")
              .append("  cape.style.display = 'inline';\n")
              .append("  cape.src = 'https://crafatar.com/capes/' + acc.uuid;\n")
              .append("  document.getElementById('detailBody').src = 'https://crafatar.com/renders/body/' + acc.uuid + '?size=180&default=MHF_Steve&overlay';\n")
              // 淡入动画
              .append("  var detailCard = document.getElementById('detailCard');\n")
              .append("  detailCard.classList.remove('fadeIn'); void detailCard.offsetWidth;\n")
              .append("  detailCard.classList.add('fadeIn');\n")
              .append("}\n")

              // 打开/关闭添加弹窗
              .append("function openAddModal(){\n")
              .append("  var modal = document.getElementById('addModal');\n")
              .append("  modal.style.display = 'flex';\n")
              .append("  setTimeout(function(){ modal.classList.add('show'); }, 10);\n")
              .append("}\n")
              .append("function closeAddModal(){\n")
              .append("  var modal = document.getElementById('addModal');\n")
              .append("  modal.classList.remove('show');\n")
              .append("  setTimeout(function(){ modal.style.display = 'none'; }, 300);\n")
              .append("}\n")

              // 打开/关闭编辑弹窗
              .append("function openEditModal(uid){\n")
              .append("  document.getElementById('editUserId').value = uid;\n")
              .append("  document.getElementById('editUsername').value = '';\n")
              .append("  document.getElementById('editAccessToken').value = '';\n")
              .append("  var modal = document.getElementById('editModal');\n")
              .append("  modal.style.display = 'flex';\n")
              .append("  setTimeout(function(){ modal.classList.add('show'); }, 10);\n")
              .append("}\n")
              .append("function closeEditModal(){\n")
              .append("  var modal = document.getElementById('editModal');\n")
              .append("  modal.classList.remove('show');\n")
              .append("  setTimeout(function(){ modal.style.display = 'none'; }, 300);\n")
              .append("}\n")

              // 打开/关闭删除弹窗
              .append("function openDeleteModal(uuid){\n")
              .append("  pendingDeleteAccountId = uuid;\n")
              .append("  var modal = document.getElementById('deleteModal');\n")
              .append("  modal.style.display = 'flex';\n")
              .append("  setTimeout(function(){ modal.classList.add('show'); }, 10);\n")
              .append("}\n")
              .append("function closeDeleteModal(){\n")
              .append("  var modal = document.getElementById('deleteModal');\n")
              .append("  modal.classList.remove('show');\n")
              .append("  setTimeout(function(){ modal.style.display = 'none'; }, 300);\n")
              .append("  pendingDeleteAccountId = null;\n")
              .append("}\n")

              // 提交添加
              .append("function submitAdd(){\n")
              .append("  var type = document.getElementById('addType').value;\n")
              .append("  var un   = document.getElementById('addUsername').value;\n")
              .append("  var pw   = document.getElementById('addPassword').value;\n")
              .append("  var params = 'action=add&type=' + encodeURIComponent(type)\n")
              .append("             + '&username=' + encodeURIComponent(un)\n")
              .append("             + '&password=' + encodeURIComponent(pw);\n")
              .append("  fetch('/manage', {\n")
              .append("    method: 'POST',\n")
              .append("    headers: {'Content-Type':'application/x-www-form-urlencoded'},\n")
              .append("    body: params\n")
              .append("  }).then(function(){\n")
              .append("    closeAddModal();\n")
              .append("    window.location.reload();\n")
              .append("  }).catch(function(){\n")
              .append("    alert('添加账户失败');\n")
              .append("  });\n")
              .append("}\n")

              // 提交编辑
              .append("function submitEdit(){\n")
              .append("  var uid = document.getElementById('editUserId').value;\n")
              .append("  var nu  = document.getElementById('editUsername').value;\n")
              .append("  var nt  = document.getElementById('editAccessToken').value;\n")
              .append("  var params = 'action=edit&userId=' + encodeURIComponent(uid)\n")
              .append("             + '&newUsername=' + encodeURIComponent(nu)\n")
              .append("             + '&newAccessToken=' + encodeURIComponent(nt);\n")
              .append("  fetch('/manage', {\n")
              .append("    method: 'POST',\n")
              .append("    headers: {'Content-Type':'application/x-www-form-urlencoded'},\n")
              .append("    body: params\n")
              .append("  }).then(function(){\n")
              .append("    closeEditModal();\n")
              .append("    window.location.reload();\n")
              .append("  }).catch(function(){\n")
              .append("    alert('编辑失败');\n")
              .append("  });\n")
              .append("}\n")

              // 高级登录动画：先转圈圈 => 成功后打勾
.append("function loginAccount(uuid, btn) {\n")
.append("  btn.disabled = true;\n")
.append("  btn.innerHTML = \"<i class='bi bi-arrow-repeat spinner'></i>\";\n")
.append("  btn.classList.add('loading');\n")  // 添加加载动画
.append("  fetch('/manage', {\n")
.append("    method:'POST',\n")
.append("    headers:{'Content-Type':'application/x-www-form-urlencoded'},\n")
.append("    body: 'action=login&userId=' + encodeURIComponent(uuid)\n")
.append("  }).then(function(resp){\n")
.append("    if (!resp.ok) {\n")
.append("      resp.text().then(function(txt){ alert('登录失败: ' + txt); });\n")
.append("      window.location.reload();\n")
.append("      return;\n")
.append("    }\n")
// **成功后切换成打勾，保持转圈圈 1 秒**
.append("    setTimeout(function() {\n")  // 延长转圈圈的时间
.append("      btn.innerHTML = \"<i class='bi bi-check-lg'></i>\";\n")
.append("      btn.classList.remove('loading');\n")
.append("      btn.classList.add('success');\n") // 打勾状态
.append("    }, 1000);  // 转圈圈持续 1 秒钟\n")
.append("    setTimeout(function() {\n")
// **打勾后 500ms 才变“已登录”**
.append("      btn.innerHTML = \"已登录\";\n")
.append("      btn.classList.remove('success');\n")
.append("      btn.classList.add('logged');\n")
.append("      btn.disabled = true;\n")
.append("    }, 1500);  // 延迟 500ms 才切换文字\n")
.append("    document.querySelectorAll('.login-btn').forEach(function(b){\n")
.append("      if (b.getAttribute('data-uuid') !== uuid) {\n")
.append("        b.innerHTML = \"登录\";\n")
.append("        b.classList.remove('logged', 'success', 'loading');\n")
.append("        b.disabled = false;\n")
.append("      }\n")
.append("    });\n")
.append("    selectAccount(uuid);\n")
.append("  }).catch(function(){\n")
.append("    alert('登录失败');\n")
.append("    window.location.reload();\n")
.append("  });\n")
.append("}\n")



              // 确认删除
              .append("function confirmDelete(){\n")
              .append("  if(!pendingDeleteAccountId) return;\n")
              .append("  var params = 'action=delete&userId=' + encodeURIComponent(pendingDeleteAccountId);\n")
              .append("  fetch('/manage', {\n")
              .append("    method: 'POST',\n")
              .append("    headers: {'Content-Type':'application/x-www-form-urlencoded'},\n")
              .append("    body: params\n")
              .append("  }).then(function(resp){\n")
              .append("    if(!resp.ok){\n")
              .append("      resp.text().then(function(txt){ alert('删除失败: ' + txt); });\n")
              .append("      closeDeleteModal();\n")
              .append("      return;\n")
              .append("    }\n")
              .append("    var elem = document.getElementById('account-' + pendingDeleteAccountId);\n")
              .append("    if(elem){ elem.remove(); }\n")
              .append("    // 如果删除的账号正是右侧展示的 => 清空\n")
              .append("    if(document.getElementById('detailUUID').innerText.indexOf(pendingDeleteAccountId) !== -1){\n")
              .append("      document.getElementById('detailUUID').innerText      = 'UUID: -';\n")
              .append("      document.getElementById('detailUsername').innerText = '用户名: -';\n")
              .append("      document.getElementById('detailTime').innerText     = '添加时间: -';\n")
              .append("      document.getElementById('detailHead').src  = 'https://crafatar.com/avatars/00000000?size=64&default=MHF_Steve&overlay';\n")
              .append("      document.getElementById('detailSkin').src  = 'https://crafatar.com/skins/00000000?size=64&default=MHF_Steve';\n")
              .append("      document.getElementById('detailCape').src  = 'https://crafatar.com/capes/00000000';\n")
              .append("      document.getElementById('detailBody').src  = 'https://crafatar.com/renders/body/00000000?size=180&default=MHF_Steve&overlay';\n")
              .append("    }\n")
              .append("    closeDeleteModal();\n")
              .append("  }).catch(function(){\n")
              .append("    alert('删除失败');\n")
              .append("    closeDeleteModal();\n")
              .append("  });\n")
              .append("}\n")

              // ESC 关闭弹窗
              .append("document.addEventListener('keydown', function(e){\n")
              .append("  if(e.key === 'Escape') {\n")
              .append("    closeAddModal();\n")
              .append("    closeEditModal();\n")
              .append("    closeDeleteModal();\n")
              .append("  }\n")
              .append("});\n")

              .append("</script>\n")
              .append("</body>\n")
              .append("</html>\n");

            return sb.toString();
        }
    }

    /**
     * Account 数据类
     */
    private static class Account {
        private String username;
        private String userId;
        private String accessToken;
        private String addedTime;
        private String type; // "official" or "offline"

        public Account(String username, String userId, String accessToken, String addedTime) {
            this.username = username;
            this.userId = userId;
            this.accessToken = accessToken;
            this.addedTime = addedTime;
            this.type = (accessToken != null && !accessToken.isEmpty()) ? "official" : "offline";
        }

        public String getUsername()     { return username; }
        public String getUserId()       { return userId; }
        public String getAccessToken()  { return accessToken; }
        public String getAddedTime()    { return addedTime; }
        public String getType()         { return type; }
        public void setUsername(String username)         { this.username = username; }
        public void setAccessToken(String accessToken)   { this.accessToken = accessToken; }

        // 左侧的“头颅”渲染图
        public String getHeadUrl() {
            return "https://crafatar.com/renders/head/" + userId + "?size=128&default=MHF_Steve&overlay";
        }
    }
}
