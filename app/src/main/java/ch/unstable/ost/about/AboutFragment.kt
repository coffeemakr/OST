package ch.unstable.ost.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ch.unstable.ost.BuildConfig

import ch.unstable.ost.R
import kotlinx.android.synthetic.main.fragment_about.*

/**
 * A placeholder fragment containing a simple view.
 */
class AboutFragment : Fragment() {

    private val appLicense = StandardLicenses.GPL3;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app_version.text = BuildConfig.VERSION_NAME

        github_link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.context.getString(R.string.github_url)))
            context!!.startActivity(intent)
        }

        app_read_license.setOnClickListener {
            LicenseFragment.showLicense(it.context, appLicense)
        }
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
