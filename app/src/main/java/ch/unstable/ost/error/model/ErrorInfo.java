package ch.unstable.ost.error.model;


import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class ErrorInfo {

    public static final ErrorInfo EMPTY = new ErrorInfo((String) null);

    @Nullable
    private final Throwable exception;
    @Nullable
    private final String stackTrace;

    private final Date timestamp = new Date();

    public ErrorInfo(@Nullable String stackTrace) {
        this.exception = null;
        this.stackTrace = stackTrace;
    }

    public ErrorInfo(@Nullable Throwable exception) {
        this.exception = exception;
        this.stackTrace = null;
    }

    @Nullable
    public Throwable getException() {
        return exception;
    }

    public String getStackTrace() {
        if (exception == null && stackTrace == null) {
            return "";
        } else if(stackTrace != null) {
            return stackTrace;
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            return sw.toString();
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
