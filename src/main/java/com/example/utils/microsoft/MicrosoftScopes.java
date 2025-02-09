package com.example.utils.microsoft;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MicrosoftScopes {
    public static final String USER_READ = "user.read";
    public static final String USER_READWRITE = "user.readwrite";
    public static final String MAIL_READ = "mail.read";
    public static final String MAIL_READWRITE = "mail.readwrite";
    public static final String MAIL_SEND = "mail.send";
    public static final String CALENDARS_READ = "calendars.read";
    public static final String CALENDARS_READWRITE = "calendars.readwrite";
    public static final String FILES_READ = "files.read";
    public static final String FILES_READWRITE = "files.readwrite";
    public static final String GROUP_READ_ALL = "group.read.all";
    public static final String GROUP_READWRITE_ALL = "group.readwrite.all";
    public static final String DIRECTORY_READ_ALL = "directory.read.all";
    public static final String DIRECTORY_READWRITE_ALL = "directory.readwrite.all";
    public static final String XBOX_LIVE_SIGNIN = "XboxLive.signin";
    public static final String XBOX_LIVE_READ = "XboxLive.read";
    public static final String XBOX_LIVE_WRITE = "XboxLive.write";
    public static final String OPENID = "openid";
    public static final String PROFILE = "profile";
    public static final String EMAIL = "email";
    public static final String OFFLINE_ACCESS = "offline_access";

    private final Set<String> scopes = new HashSet<>();

    public MicrosoftScopes addScope(String scope) {
        this.scopes.add(scope);

        return this;
    }

    public MicrosoftScopes addScopes(String... scopes) {
        Collections.addAll(this.scopes, scopes);

        return this;
    }

    public String build() {
        return String.join(" ", this.scopes);
    }

    public boolean contains(String scope) {
        return this.scopes.contains(scope);
    }

    public MicrosoftScopes clear() {
        this.scopes.clear();

        return this;
    }
}
