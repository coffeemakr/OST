package ch.unstable.ost.error.model;


public class ErrorReport {

    private final ErrorInfo error;
    private final BuildInfo build;
    private final AppInfo app;
    private final AndroidInfo android;

    public ErrorReport(ErrorInfo error, AppInfo app, BuildInfo build, AndroidInfo android) {
        this.error = error;
        this.build = build;
        this.app = app;
        this.android = android;
    }

    public BuildInfo getBuild() {
        return build;
    }

    public AppInfo getApp() {
        return app;
    }

    public AndroidInfo getAndroid() {
        return android;
    }

    public ErrorInfo getError() {
        return error;
    }
}
