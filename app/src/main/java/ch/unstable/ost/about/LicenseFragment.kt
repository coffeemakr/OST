package ch.unstable.ost.about


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.webkit.WebView
import android.widget.TextView
import ch.unstable.ost.R
import java.util.*

/**
 * Fragment containing the software licenses
 */
class LicenseFragment : Fragment() {
    private var softwareComponents: Array<SoftwareComponent> = emptyArray()
    private var mComponentForContextMenu: SoftwareComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        softwareComponents = arguments.getParcelableArray(ARG_COMPONENTS) as Array<SoftwareComponent>

        // Sort components by name
        Arrays.sort(softwareComponents) { o1, o2 -> o1.name.compareTo(o2.name) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_licenses, container, false)
        val softwareComponentsView = rootView.findViewById<ViewGroup>(R.id.software_components)

        for (component in softwareComponents) {
            val componentView = inflater.inflate(R.layout.item_software_component, container, false)
            val softwareName = componentView.findViewById<TextView>(R.id.name)
            val copyright = componentView.findViewById<TextView>(R.id.copyright)
            softwareName.text = component.name
            copyright.text = context.getString(R.string.copyright,
                    component.years,
                    component.copyrightOwner,
                    component.license.abbreviation)

            componentView.tag = component
            componentView.setOnClickListener {
                showLicense(it.context, component.license)
            }
            softwareComponentsView.addView(componentView)
            registerForContextMenu(componentView)

        }
        return rootView
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        val inflater = activity.menuInflater
        val component = v.tag as SoftwareComponent
        menu.setHeaderTitle(component.name)
        inflater.inflate(R.menu.software_component, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
        mComponentForContextMenu = v.tag as SoftwareComponent
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // item.getMenuInfo() is null so we use the tag of the view
        val component = mComponentForContextMenu ?: return false
        when (item.itemId) {
            R.id.action_website -> {
                openWebsite(component.link)
                return true;
            }
            R.id.action_show_license -> {
                showLicense(context, component.license)
                return true;
            }
        }
        return false
    }

    private fun openWebsite(componentLink: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(componentLink))
        startActivity(browserIntent)
    }

    companion object {

        private val ARG_COMPONENTS = "components"

        fun newInstance(softwareComponents: Array<SoftwareComponent>?): LicenseFragment {
            if (softwareComponents == null) {
                throw NullPointerException("softwareComponents is null")
            }
            val fragment = LicenseFragment()
            val bundle = Bundle()
            bundle.putParcelableArray(ARG_COMPONENTS, softwareComponents)
            fragment.arguments = bundle
            return fragment
        }

        /**
         * Shows a popup containing the license
         * @param context the context to use
         * @param license the license to show
         */
        fun showLicense(context: Context, license: License) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(license.name)

            val wv = WebView(context)
            wv.loadUrl(license.contentUri.toString())
            alert.setView(wv)
            alert.setNegativeButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
            alert.show()
        }
    }
}
