package ch.unstable.ost.error;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.R;
import ch.unstable.ost.error.model.AndroidInfo;
import ch.unstable.ost.error.model.AppInfo;
import ch.unstable.ost.error.model.BuildInfo;
import ch.unstable.ost.error.model.ErrorInfo;
import ch.unstable.ost.error.model.ErrorReport;
import ch.unstable.ost.error.model.SignatureTypeAdapter;

public class ErrorReportActivity extends AppCompatActivity {

    public static final String EXTRA_EXCEPTION = "ErrorReportActivity.EXTRA_EXCEPTION";
    private static final String TAG = ErrorReportActivity.class.getSimpleName();
    private ErrorReport mReport;

    private static void bindAppInfo(Context context, AppInfoViewHolder viewHolder, AppInfo appInfo) {
        viewHolder.appPackage.setText(appInfo.getId());
        viewHolder.appVersion.setText(context.getString(R.string.format_app_version, appInfo.getVersion(), appInfo.getVersionCode()));
    }

    private static void bindBuildInfo(Context context, BuildInfoViewHolder viewHolder, BuildInfo buildInfo) {
        viewHolder.buildId.setText(buildInfo.getId());
    }

    private static void bindAndroidInfo(Context context, AndroidVersionViewHolder viewHolder, AndroidInfo androidInfo) {
        viewHolder.androidSDK.setText(String.format(Locale.getDefault(), "%d", androidInfo.getSdk()));
        viewHolder.androidRelease.setText(androidInfo.getRelease());
    }

    private static ErrorReport buildReport(Context context, ErrorInfo errorInfo) {
        BuildInfo buildInfo = new BuildInfo();
        AppInfo appInfo = new AppInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID);
        AndroidInfo androidinfo = new AndroidInfo();
        return new ErrorReport(errorInfo, appInfo, buildInfo, androidinfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendErrorReport();
            }
        });

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        ErrorInfo errorInfo = getErrorInfoFromIntent(getIntent());
        mReport = buildReport(this, errorInfo);

        bindAndroidInfo(this, new AndroidVersionViewHolder(this), mReport.getAndroid());
        bindBuildInfo(this, new BuildInfoViewHolder(this), mReport.getBuild());
        bindAppInfo(this, new AppInfoViewHolder(this), mReport.getApp());
        bindErrorInfo(this, new ErrorViewHolder(this), mReport.getError());
    }

    @NonNull
    private ErrorInfo getErrorInfoFromIntent(Intent intent) {
        if (intent != null) {
            String stackTrace = CustomActivityOnCrash.getStackTraceFromIntent(intent);
            // getStackTrace can return null but is annotated as NonNull
            //noinspection ConstantConditions
            if (stackTrace != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Got stacktrace from stacktrace: " + stackTrace);
                }
                return new ErrorInfo(stackTrace);
            }

            Throwable exception = (Throwable) intent.getSerializableExtra(EXTRA_EXCEPTION);
            if (exception != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Got stacktrace from exception");
                }
                return new ErrorInfo(exception);
            }
        }
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "No stacktrace/throwable provided");
        }
        return ErrorInfo.EMPTY;
    }


    public String getErrorReportJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Signature.class, new SignatureTypeAdapter())
                .create();
        return gson.toJson(mReport);
    }

    public void onSendErrorReport() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", getString(R.string.error_report_email), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.error_report_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getErrorReportJson());
        startActivity(Intent.createChooser(emailIntent, getString(R.string.error_report_title)));

    }

    private void bindErrorInfo(Context context, ErrorViewHolder viewHolder, ErrorInfo errorInfo) {
        viewHolder.errorStackTrace.setText(errorInfo.getStackTrace());
        viewHolder.errorStackTrace.setHorizontallyScrolling(true);
    }

    private static class AndroidVersionViewHolder {
        private final TextView androidRelease;
        private final TextView androidSDK;

        AndroidVersionViewHolder(Activity rootView) {
            this.androidRelease = rootView.findViewById(R.id.androidRelease);
            this.androidSDK = rootView.findViewById(R.id.androidSDK);
        }
    }

    private static class BuildInfoViewHolder {
        private final TextView buildId;

        public BuildInfoViewHolder(Activity activity) {
            this.buildId = activity.findViewById(R.id.appBuildId);
        }
    }

    private static class AppInfoViewHolder {
        private final TextView appVersion;
        private final TextView appPackage;

        public AppInfoViewHolder(Activity activity) {
            this.appVersion = activity.findViewById(R.id.appVersion);
            this.appPackage = activity.findViewById(R.id.appPackage);
        }
    }

    private static class ErrorViewHolder {
        private final TextView errorStackTrace;

        public ErrorViewHolder(Activity activity) {
            this.errorStackTrace = activity.findViewById(R.id.stackTrace);
        }
    }
}
