package ch.unstable.ost.preference

import android.os.Bundle
import android.preference.PreferenceFragment
import ch.unstable.ost.R

class ContentSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_content)
    }
}