package com.example.utils.microsoft;

import com.example.utils.URLBuilder;

public class MicrosoftLogin {
    private static final String AUTHORIZE_URL = "https://login.live.com/oauth20_authorize.srf";
    private static final String TOKEN_URL = "https://login.live.com/oauth20_token.srf";

    public static String getAuthorizationUrl(String redirectUri, String responseType, String scope, String prompt) {
        return new URLBuilder(AUTHORIZE_URL)
                .parameter("client_id", MicrosoftAAD.CLIENT_ID)
                .parameter("client_secret", MicrosoftAAD.CLIENT_SECRET_VALUE)
                .parameter("redirect_uri", redirectUri)
                .parameter("response_type", responseType)
                .parameter("scope", scope)
                .parameter("prompt", prompt)
                .build();
    }

    public static String getAuthorizationUrl(String redirectUri, String responseType, MicrosoftScopes scopes, String prompt) {
        return getAuthorizationUrl(redirectUri, responseType, scopes.build(), prompt);
    }

    public static String getAuthorizationCodeUrl(String code, String redirectUri) {
        return new URLBuilder(TOKEN_URL)
                .parameter("client_id", MicrosoftAAD.CLIENT_ID)
                .parameter("client_secret", MicrosoftAAD.CLIENT_SECRET_VALUE)
                .parameter("redirect_uri", redirectUri)
                .parameter("grant_type", "authorization_code")
                .parameter("code", code)
                .build();
    }
}
