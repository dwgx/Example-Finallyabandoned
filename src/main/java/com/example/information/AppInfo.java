package com.example.information;

public class AppInfo {
    public static final String ID = "example";
    public static final String NAME = "Example";
    public static final String NAME_LOWERCASE = NAME.toLowerCase();
    public static final String AUTHOR = "EzDiaoL";
    public static final Version VERSION = new Version(1, 0, 0);
    public static final VersionLifecycle LIFECYCLE = VersionLifecycle.PRE_ALPHA;

    private AppInfo() {}

    public record Version(int major, int minor, int patch) {
        @Override
        public String toString() {
            return String.format("%d.%d.%d", major, minor, patch);
        }
    }

    public static boolean isDevelopment() {
        return LIFECYCLE.equals(VersionLifecycle.PRE_ALPHA);
    }
}
