package ch.unstable.ost.error.model;


public class AppInfo {
    private final String version;
    private final int versionCode;
    private final String id;

    public AppInfo(String version, int versionCode, String id) {
        this.version = version;
        this.versionCode = versionCode;
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getId() {
        return id;
    }

}
