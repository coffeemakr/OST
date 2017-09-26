package ch.unstable.ost.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView version = rootView.findViewById(R.id.app_version);
        version.setText(BuildConfig.VERSION_NAME);

        View githubLink = rootView.findViewById(R.id.github_link);
        githubLink.setOnClickListener(new OnGithubLinkClickListener());

        View licenseLink = rootView.findViewById(R.id.app_read_license);
        licenseLink.setOnClickListener(new OnReadFullLicenseClickListener());
        return rootView;
    }

    private static class OnGithubLinkClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            final Context context = view.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.github_url)));
            context.startActivity(intent);
        }
    }

    private static class OnReadFullLicenseClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LicenseFragment.Companion.showLicense(v.getContext(), StandardLicenses.GPL3);
        }
    }
}
