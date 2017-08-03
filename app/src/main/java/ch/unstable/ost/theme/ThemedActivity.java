package ch.unstable.ost.theme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public abstract class ThemedActivity extends AppCompatActivity {

    private boolean mPendingThemeChange;
    private boolean mResumed;
    private final ThemeHelper.OnThemeChangeListener onThemeChangeListener = new ThemeHelper.OnThemeChangeListener() {
        @Override
        void onThemeChanged() {
            if (mResumed) {
                recreate();
            } else {
                mPendingThemeChange = true;
            }
        }
    };

    private Handler mHandler;


    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.setTheme(this);
        mHandler = new Handler();
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onThemeChangeListener);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onThemeChangeListener);
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        if (mPendingThemeChange) {
            mPendingThemeChange = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            });
        }
    }

    @CallSuper
    @Override
    protected void onPause() {
        mResumed = false;
        super.onPause();
    }
}
