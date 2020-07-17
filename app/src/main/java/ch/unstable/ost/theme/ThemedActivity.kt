package ch.unstable.ost.theme

import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import ch.unstable.ost.theme.ThemeHelper.OnThemeChangeListener

abstract class ThemedActivity : AppCompatActivity() {
    private var mPendingThemeChange = false
    private var mResumed = false
    private val onThemeChangeListener: OnThemeChangeListener = object : OnThemeChangeListener() {
        public override fun onThemeChanged() {
            if (mResumed) {
                recreate()
            } else {
                mPendingThemeChange = true
            }
        }
    }
    private var mHandler: Handler? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.setTheme(this)
        super.onCreate(savedInstanceState)
        mHandler = Handler()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onThemeChangeListener)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onThemeChangeListener)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        mResumed = true
        if (mPendingThemeChange) {
            mPendingThemeChange = false
            mHandler!!.post { recreate() }
        }
    }

    @CallSuper
    override fun onPause() {
        mResumed = false
        super.onPause()
    }
}