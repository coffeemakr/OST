package ch.unstable.ost.preference

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import ch.unstable.ost.R

class AppearanceSettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mOnThemeChangedListener: OnThemeChangedListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mOnThemeChangedListener = context as OnThemeChangedListener
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mOnThemeChangedListener = activity as OnThemeChangedListener
    }

    override fun onDetach() {
        super.onDetach()
        mOnThemeChangedListener = null
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_appearance)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
        if (key == PreferenceKeys.KEY_THEME) {
            if (mOnThemeChangedListener != null) {
                mOnThemeChangedListener!!.onThemeChanged()
            }
        }
    }

    internal interface OnThemeChangedListener {
        fun onThemeChanged()
    }
}