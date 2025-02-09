package com.example.utils;

import com.example.utils.text.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLBuilder {
    private final StringBuilder baseUrl;
    private final StringBuilder queryParameters;

    public URLBuilder(String baseUrl) {
        this.baseUrl = new StringBuilder(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
        this.queryParameters = new StringBuilder();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public URLBuilder path(String path) {
        if (path != null && !path.isEmpty()) {
            if (!this.baseUrl.toString().endsWith("/")) {
                this.baseUrl.append("/");
            }
            this.baseUrl.append(path.replaceAll("^/|/$", ""));
        }
        return this;
    }

    public URLBuilder parameter(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            if (this.queryParameters.isEmpty()) {
                this.queryParameters.append("?");
            } else {
                this.queryParameters.append("&");
            }
            this.queryParameters.append(encode(key)).append("=").append(encode(value));
        }
        return this;
    }

    public StringBuilder getBaseUrl() {
        return this.baseUrl;
    }

    public StringBuilder getQueryParameters() {
        return this.queryParameters;
    }

    public String build() {
        return this.baseUrl.toString() + this.queryParameters.toString();
    }
}
