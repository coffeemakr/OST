package ch.unstable.ost.error

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.Signature
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import ch.unstable.ost.BuildConfig
import ch.unstable.ost.R
import ch.unstable.ost.error.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import java.util.*

class ErrorReportActivity : AppCompatActivity() {
    private var mReport: ErrorReport? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_report)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View? -> onSendErrorReport() }
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        val errorInfo = getErrorInfoFromIntent(getIntent())
        mReport = buildReport(errorInfo)
        bindAndroidInfo(AndroidVersionViewHolder(this), mReport!!.android)
        bindBuildInfo(BuildInfoViewHolder(this), mReport!!.build)
        bindAppInfo(this, AppInfoViewHolder(this), mReport!!.app)
        bindErrorInfo(ErrorViewHolder(this), mReport!!.error)
    }

    private fun getErrorInfoFromIntent(intent: Intent?): ErrorInfo {
        if (intent != null) {
            val stackTrace = CustomActivityOnCrash.getStackTraceFromIntent(intent)
            // getStackTrace can return null but is annotated as NonNull
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Got stacktrace from stacktrace: $stackTrace")
            }
            return ErrorInfo(stackTrace)
        }
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "No stacktrace/throwable provided")
        }
        return ErrorInfo.EMPTY
    }

    val errorReportJson: String
        get() {
            val gson = GsonBuilder()
                    .registerTypeAdapter(Signature::class.java, SignatureTypeAdapter())
                    .create()
            return gson.toJson(mReport)
        }

    fun onSendErrorReport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", getString(R.string.error_report_email), null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.error_report_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, errorReportJson)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.error_report_title)))
    }

    private fun bindErrorInfo(viewHolder: ErrorViewHolder, errorInfo: ErrorInfo) {
        viewHolder.errorStackTrace.text = errorInfo.getStackTrace()
        viewHolder.errorStackTrace.setHorizontallyScrolling(true)
    }

    private class AndroidVersionViewHolder internal constructor(rootView: Activity) {
        val androidRelease: TextView
        val androidSDK: TextView

        init {
            androidRelease = rootView.findViewById(R.id.androidRelease)
            androidSDK = rootView.findViewById(R.id.androidSDK)
        }
    }

    private class BuildInfoViewHolder(activity: Activity) {
        val buildId: TextView

        init {
            buildId = activity.findViewById(R.id.appBuildId)
        }
    }

    private class AppInfoViewHolder(activity: Activity) {
        val appVersion: TextView
        val appPackage: TextView

        init {
            appVersion = activity.findViewById(R.id.appVersion)
            appPackage = activity.findViewById(R.id.appPackage)
        }
    }

    private class ErrorViewHolder(activity: Activity) {
        val errorStackTrace: TextView

        init {
            errorStackTrace = activity.findViewById(R.id.stackTrace)
        }
    }

    companion object {
        const val EXTRA_EXCEPTION = "ErrorReportActivity.EXTRA_EXCEPTION"
        private val TAG = ErrorReportActivity::class.java.simpleName
        private fun bindAppInfo(context: Context, viewHolder: AppInfoViewHolder, appInfo: AppInfo) {
            viewHolder.appPackage.text = appInfo.id
            viewHolder.appVersion.text = context.getString(R.string.format_app_version, appInfo.version, appInfo.versionCode)
        }

        private fun bindBuildInfo(viewHolder: BuildInfoViewHolder, buildInfo: BuildInfo) {
            viewHolder.buildId.text = buildInfo.id
        }

        private fun bindAndroidInfo(viewHolder: AndroidVersionViewHolder, androidInfo: AndroidInfo) {
            viewHolder.androidSDK.text = String.format(Locale.getDefault(), "%d", androidInfo.sdk)
            viewHolder.androidRelease.text = androidInfo.release
        }

        private fun buildReport(errorInfo: ErrorInfo): ErrorReport {
            val buildInfo = BuildInfo()
            val appInfo = AppInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)
            val androidinfo = AndroidInfo()
            return ErrorReport(errorInfo, appInfo, buildInfo, androidinfo)
        }
    }
}