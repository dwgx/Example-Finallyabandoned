package com.example.mod.utils;/*package com.example.mod.utils;

import com.example.mod.handlers.MicrosoftHandler.Account; // 导入正确的 Account 类
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CookieManager {
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveAccounts(List<Account> accounts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ACCOUNTS_FILE), accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Account> loadAccounts() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Account>>() {}); // 使用正确的类型
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Map<String, String> parsePostData(InputStream requestBody) {
        Map<String, String> data = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            // 解析查询字符串
            String[] pairs = body.toString().split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    data.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}*/
