package ch.unstable.ost

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig
import ch.unstable.ost.error.ErrorReportActivity

class OSTApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CaocConfig.Builder.create()
                .errorActivity(ErrorReportActivity::class.java)
                .apply()
    }
}