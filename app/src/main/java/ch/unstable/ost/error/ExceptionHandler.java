package ch.unstable.ost.error;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = ExceptionHandler.class.getSimpleName();
    private final Context context;
    private final Handler handler;
    private final Thread.UncaughtExceptionHandler previousHandler;

    @MainThread
    public ExceptionHandler(Context context, @Nullable Thread.UncaughtExceptionHandler previousHandler) {
        this.previousHandler = previousHandler;
        this.context = context;
        this.handler = new Handler();
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        Log.e(TAG, "uncaught exception in " + t.toString(), e);
        if (t == Looper.getMainLooper().getThread()) {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Looper.loop();
                }
            }.start();
        }
        /*if(previousHandler != null) {
            previousHandler.uncaughtException(t, e);
        }*/
        handler.post(new Runnable() {
            @Override
            public void run() {
                startErrorActivity(e);
            }
        });
    }

    private void startErrorActivity(Throwable exception) {
        Log.d(TAG, "Starting error activity");
        Intent intent = new Intent(context, ErrorReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ErrorReportActivity.EXTRA_EXCEPTION, exception);
        context.startActivity(intent);
    }
}
