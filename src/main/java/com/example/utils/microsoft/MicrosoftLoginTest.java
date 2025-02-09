package com.example.utils.microsoft;

public class MicrosoftLoginTest {
    public static void main(String[] args) {
        System.out.println(
                MicrosoftLogin.getAuthorizationUrl(
                        "https://example.l.wiki/auth/mc",
                        "code",
                        new MicrosoftScopes().addScopes(MicrosoftScopes.XBOX_LIVE_SIGNIN, MicrosoftScopes.OFFLINE_ACCESS),
                        "select_account"
                )
        );
    }
}
