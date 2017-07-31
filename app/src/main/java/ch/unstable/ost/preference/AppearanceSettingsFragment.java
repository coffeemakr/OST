package ch.unstable.ost.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import ch.unstable.ost.R;


public final class AppearanceSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private OnThemeChangedListener mOnThemeChangedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnThemeChangedListener = (OnThemeChangedListener) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnThemeChangedListener = (OnThemeChangedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnThemeChangedListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_appearance);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        if (key.equals(PreferenceKeys.KEY_THEME)) {
            if (mOnThemeChangedListener != null) {
                mOnThemeChangedListener.onThemeChanged();
            }
        }
    }


    interface OnThemeChangedListener {
        void onThemeChanged();
    }
}
