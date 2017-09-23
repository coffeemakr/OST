package ch.unstable.ost;


import android.content.Context;
import android.content.Intent;

import ch.unstable.ost.error.ErrorReportActivity;

public enum NavHelper {
    ;

    public static void startErrorActivity(Context context, Throwable throwable) {
        Intent intent = new Intent(context, ErrorReportActivity.class);
        intent.putExtra(ErrorReportActivity.EXTRA_EXCEPTION, throwable);
        context.startActivity(intent);
    }
}
