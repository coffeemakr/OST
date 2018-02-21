package ch.unstable.ost.about


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.*
import android.webkit.WebView
import android.widget.TextView
import ch.unstable.ost.R
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Fragment containing the software licenses
 */
class LicenseFragment : Fragment() {

    private var mComponentForContextMenu: SoftwareComponent? = null
    private var softwareComponentsAdapter: LicenseFragment.SoftwareComponentsAdapter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val softwareComponents = arguments!!.getParcelableArrayList<SoftwareComponent>(ARG_COMPONENTS)
        // Sort components by name
        Collections.sort(softwareComponents) { o1, o2 -> o1.name.compareTo(o2.name) }
        softwareComponentsAdapter = SoftwareComponentsAdapter(this)
        softwareComponentsAdapter!!.setElements(softwareComponents)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view.findViewById<RecyclerView>(R.id.software_components)) {
            layoutManager = android.support.v7.widget.LinearLayoutManager(context, android.support.v7.widget.LinearLayoutManager.VERTICAL, false);
            adapter = softwareComponentsAdapter
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflater = activity!!.menuInflater!!
        val component = v.tag!! as SoftwareComponent
        menu.setHeaderTitle(component.name)
        inflater.inflate(R.menu.software_component, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
        mComponentForContextMenu = component
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
                showLicense(context!!, component.license)
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

        fun newInstance(softwareComponents: Collection<SoftwareComponent>): LicenseFragment {
            val fragment = LicenseFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_COMPONENTS, ArrayList(softwareComponents))
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
            alert.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            alert.show()
        }
    }


    class SoftwareComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val copyright: TextView = itemView.findViewById<TextView>(R.id.copyright)
    }

    class SoftwareComponentsAdapter(val fragment: Fragment) : SingleTypeSimplerAdapter<SoftwareComponent, SoftwareComponentViewHolder>() {

        override val layout = R.layout.item_software_component

        override fun onBindViewHolder(viewHolder: SoftwareComponentViewHolder, element: SoftwareComponent, position: Int) {
            val context = viewHolder.itemView.context;
            with(viewHolder) {
                copyright.text = context.getString(R.string.copyright,
                        element.years,
                        element.copyrightOwner,
                        element.license.abbreviation)
                name.text = element.name

                with(itemView) {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        showLicense(it.context, element.license)
                    }
                    tag = element
                    fragment.registerForContextMenu(itemView)
                }

            }
        }

        override fun onCreateViewHolder(itemView: View) = SoftwareComponentViewHolder(itemView)

    }
}
