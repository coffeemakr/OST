package ch.unstable.ost.theme

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import ch.unstable.ost.R
import ch.unstable.ost.preference.PreferenceKeys

object ThemeHelper {

    abstract class OnThemeChangeListener : SharedPreferences.OnSharedPreferenceChangeListener {
        abstract fun onThemeChanged()
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == PreferenceKeys.KEY_THEME) {
                onThemeChanged()
            }
        }
    }

    private const val TAG = "ThemeHelper"

    @StyleRes
    private val DEFAULT_THEME = R.style.GreenDarkTheme
    fun setTheme(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val currentTheme = preferences.getString(PreferenceKeys.KEY_THEME, null)
        val style: Int = if (currentTheme == null) {
            DEFAULT_THEME
        } else {
            getThemeStyle(context.resources, currentTheme)
        }
        context.setTheme(style)
    }

    @StyleRes
    private fun getThemeStyle(resources: Resources, currentTheme: String?): Int {
        if (currentTheme == null) {
            throw NullPointerException("currentTheme is null")
        }
        val styles = resources.obtainTypedArray(R.array.theme_styles)
        val values = resources.getStringArray(R.array.theme_values)
        var i = 0
        var styleRes = 0
        for (value in values) {
            if (value == currentTheme) {
                styleRes = styles.getResourceId(i, 0)
                break
            }
            ++i
        }
        styles.recycle()
        if (styleRes == 0) {
            Log.e(TAG, "style not found (currentTheme: $currentTheme)")
            return DEFAULT_THEME
        }
        return styleRes
    }

    @DrawableRes
    fun getThemedDrawable(context: Context, @AttrRes attr: Int): Int {
        val theme = context.theme
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.resourceId
    }
}