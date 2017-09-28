package ch.unstable.ost.utils


import android.content.Context
import android.content.Intent
import ch.unstable.ost.about.AboutActivity
import ch.unstable.ost.error.ErrorReportActivity
import ch.unstable.ost.preference.SettingsActivity

fun startErrorActivity(context: Context, throwable: Throwable) {
    val intent = Intent(context, ErrorReportActivity::class.java)
    intent.putExtra(ErrorReportActivity.EXTRA_EXCEPTION, throwable)
    context.startActivity(intent)
}

fun openAbout(context: Context) {
    val intent = Intent(context, AboutActivity::class.java)
    context.startActivity(intent)
}

fun openSettings(context: Context) {
    val intent = Intent(context, SettingsActivity::class.java);
    context.startActivity(intent)
}
