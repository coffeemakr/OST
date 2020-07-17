package ch.unstable.ost.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import ch.unstable.ost.R;


public class ContentSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_content);
    }
}
