package ch.unstable.ost.error.model;


import android.os.Build;

public class BuildInfo {
    private final String id;

    public BuildInfo() {
        this(Build.ID);
    }

    public BuildInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
