package ch.unstable.ost.error.model;


import android.os.Build;

public class AndroidInfo {
    private final int sdk;
    private final String release;

    public AndroidInfo() {
        this(Build.VERSION.SDK_INT, Build.VERSION.RELEASE);
    }

    public AndroidInfo(int sdk, String release) {
        this.sdk = sdk;
        this.release = release;
    }

    public int getSdk() {
        return sdk;
    }

    public String getRelease() {
        return release;
    }
}
