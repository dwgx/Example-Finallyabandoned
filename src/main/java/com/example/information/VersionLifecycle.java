package com.example.information;

public enum VersionLifecycle {
    /** Development period **/
    PRE_ALPHA("Pre Alpha"), // aka: development release | nightly builds
    ALPHA("Alpha"),
    BETA("Beta"),
    RELEASE_CANDIDATE("Release Candidate"),

    /** Release period **/
    RTM("Release to Manufacturing"),
    GA("General Availability"),
    STABLE("Stable"),
    EOL("End Of Life");

    private final String stage;

    VersionLifecycle(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
