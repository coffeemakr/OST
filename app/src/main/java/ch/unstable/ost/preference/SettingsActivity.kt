package ch.unstable.ost.preference

import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.appcompat.app.AppCompatDelegate
import ch.unstable.ost.R
import ch.unstable.ost.preference.AppearanceSettingsFragment.OnThemeChangedListener
import ch.unstable.ost.theme.ThemeHelper

class SettingsActivity : PreferenceActivity(), OnThemeChangedListener {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.setTheme(this)
        super.onCreate(savedInstanceState)
    }

    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun onThemeChanged() {
        recreate()
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return AppearanceSettingsFragment::class.java.name == fragmentName || ContentSettingsFragment::class.java.name == fragmentName
    }
}