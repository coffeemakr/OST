package ch.unstable.ost.error.model;


import android.content.pm.Signature;

public class AppInfo {
    private final String version;
    private final int versionCode;
    private final String id;
    private final Signature[] signatures;


    public AppInfo(String version, int versionCode, String id, Signature[] signatures) {
        this.version = version;
        this.versionCode = versionCode;
        this.id = id;
        this.signatures = signatures;
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

    public Signature[] getSignatures() {
        return signatures;
    }

}
