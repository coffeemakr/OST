package ch.unstable.ost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.views.lists.station.SectionsStopsListAdapter

class SectionDetailFragment : Fragment() {
    private var sectionToShow: Section? = null
    private var stopsListAdapter: SectionsStopsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sectionToShow = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(KEY_SECTION)
        } else {
            requireArguments().getParcelable(KEY_SECTION)
        }
        checkNotNull(sectionToShow) { "section not set" }
        stopsListAdapter = SectionsStopsListAdapter().apply {
            // TODO
            setElements(emptyList())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_SECTION, sectionToShow)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_journey_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsList: RecyclerView = view.findViewById(R.id.stationsList)
        stationsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        stationsList.adapter = stopsListAdapter
    }

    companion object {
        private const val KEY_SECTION = "SectionDetailFragment.KEY_SECTION"
        fun newInstance(section: Section?): SectionDetailFragment {
            val arguments = Bundle()
            arguments.putParcelable(KEY_SECTION, section)
            val fragment = SectionDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}