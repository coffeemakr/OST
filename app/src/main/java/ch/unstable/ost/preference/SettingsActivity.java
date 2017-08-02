package ch.unstable.ost.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import java.util.List;

import ch.unstable.ost.R;
import ch.unstable.ost.theme.ThemeHelper;

public class SettingsActivity extends PreferenceActivity implements AppearanceSettingsFragment.OnThemeChangedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.setTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    public void onThemeChanged() {
        recreate();
    }


    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AppearanceSettingsFragment.class.getName().equals(fragmentName);
    }
}
