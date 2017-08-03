package ch.unstable.ost.error.model;


import android.os.Build;

public class AndroidInfo {
    private final int sdk;
    private final String release;

    public AndroidInfo() {
        this.sdk = Build.VERSION.SDK_INT;
        this.release = Build.VERSION.RELEASE;
    }

    public int getSdk() {
        return sdk;
    }

    public String getRelease() {
        return release;
    }
}
