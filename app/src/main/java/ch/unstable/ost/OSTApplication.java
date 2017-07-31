package ch.unstable.ost;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ch.unstable.ost.preference.PreferenceKeys;
import ch.unstable.ost.theme.ThemeHelper;

public class OSTApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "OSTApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        ThemeHelper.setTheme(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceKeys.KEY_THEME)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Theme changed!");
            ThemeHelper.setTheme(this);
        }
    }
}
