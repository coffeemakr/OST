package ch.unstable.ost.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.util.TypedValue;

import ch.unstable.ost.R;
import ch.unstable.ost.preference.PreferenceKeys;

public enum ThemeHelper {
    ;

    private static final String TAG = "ThemeHelper";

    @StyleRes
    private static final int DEFAULT_THEME = R.style.GreenDarkTheme;

    public static void setTheme(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentTheme = preferences.getString(PreferenceKeys.KEY_THEME, null);
        int style;
        if (currentTheme == null) {
            style = DEFAULT_THEME;
        } else {
            style = getThemeStyle(context.getResources(), currentTheme);
        }
        context.setTheme(style);
    }

    @StyleRes
    private static int getThemeStyle(Resources resources, String currentTheme) {
        if (currentTheme == null) {
            throw new NullPointerException("currentTheme is null");
        }
        TypedArray styles = resources.obtainTypedArray(R.array.theme_styles);
        String[] values = resources.getStringArray(R.array.theme_values);

        int i = 0;
        int styleRes = 0;
        for (String value : values) {
            if (value.equals(currentTheme)) {
                styleRes = styles.getResourceId(i, 0);
                break;
            }
            ++i;
        }
        styles.recycle();
        if (styleRes == 0) {
            Log.e(TAG, "style not found (currentTheme: " + currentTheme + ")");
            return DEFAULT_THEME;
        }
        return styleRes;
    }

    @DrawableRes
    public static int getThemedDrawable(Context context, @AttrRes int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }

    public static abstract class OnThemeChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        abstract void onThemeChanged();

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferenceKeys.KEY_THEME)) {
                onThemeChanged();
            }
        }
    }
}
