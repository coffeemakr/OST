package ch.unstable.ost.utils


import android.content.Context
import android.content.Intent

import ch.unstable.ost.ConnectionDetailActivity
import ch.unstable.ost.about.AboutActivity
import ch.unstable.ost.error.ErrorReportActivity

object NavHelper {
    fun startErrorActivity(context: Context, throwable: Throwable) {
        val intent = Intent(context, ErrorReportActivity::class.java)
        intent.putExtra(ErrorReportActivity.EXTRA_EXCEPTION, throwable)
        context.startActivity(intent)
    }

    fun openAbout(context: Context) {
        val intent = Intent(context, AboutActivity::class.java)
        context.startActivity(intent)
    }
}
