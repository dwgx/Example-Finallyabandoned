// src/main/java/com/example/mod/handlers/Account.java
package com.example.mod.handlers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 定义用户账号信息类
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class Account {
    private String username;
    private String userId;
    private String accessToken;

    // Jackson 需要的空构造器
    public Account() {}

    public Account(String username, String userId, String accessToken) {
        this.username = username;
        this.userId = userId;
        this.accessToken = accessToken;
    }

    // ========== GETTERS ==========

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 生成头像URL，渲染头部 3D
     */
    public String getAvatarUrl() {
        return "https://crafatar.com/renders/head/" + userId + "?size=128&default=MHF_Steve&overlay";
    }

    /**
     * 生成披风URL
     */
    public String getCloakUrl() {
        return "https://crafatar.com/capes/" + userId + "?default=" + userId;
    }

    /**
     * 生成 2D 头像/3D模型URL
     */
    public String getThreeDModelUrl() {
        return "https://crafatar.com/avatars/" + userId + "?size=128&overlay";
    }

    // ========== SETTERS ==========

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
