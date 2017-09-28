package ch.unstable.ost.views.lists.section


import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ch.unstable.ost.BuildConfig
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.utils.TimeDateUtils

private const val TAG = "SectionListAdapters"
private const val JOURNEY_VIEW_TYPE = 1
private const val WALK_VIEW_TYPE = 2


class SectionListAdapter : RecyclerView.Adapter<SectionListAdapter.SectionViewHolder>() {

    var sections:List<Section> = ArrayList()
        set(value) {
            field = ArrayList(value)
            notifyDataSetChanged()
        }

    var onJourneyClickedListener: OnSectionClickedListener? = null

    private val onJourneyItemClickListener = View.OnClickListener {
        val section = it.tag as Section?
        if (section == null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Got tag null for view: " + it)
            return@OnClickListener
        }
        onJourneyClickedListener?.onSectionClicked(section)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView: View
        when (viewType) {
            JOURNEY_VIEW_TYPE -> {
                itemView = inflater.inflate(R.layout.item_connection_section_journey, parent, false)
                return JourneyViewHolder(itemView)
            }
            WALK_VIEW_TYPE -> {
                itemView = inflater.inflate(R.layout.item_connection_section_walk, parent, false)
                return WalkSectionViewHolder(itemView)
            }
        }
        throw IllegalStateException("unknown viewType: " + viewType)
    }

    private fun onBindJourneyViewHolder(holder: JourneyViewHolder, section: Section) {
        with(holder) {
            arrivalStationName.text = section.arrivalLocation.name
            departureStationName.text = section.departureLocation.name
            arrivalTime.text = TimeDateUtils.formatTime(section.arrivalDate)
            departureTime.text = TimeDateUtils.formatTime(section.departureDate)
            productName.text = section.lineShortName
            endDestination.text = section.headsign
            departurePlatform.text = section.departurePlatform
            arrivalPlatform.text = section.arrivalPlatform
            itemView.tag = section
            itemView.setOnClickListener(onJourneyItemClickListener)
        }
    }

    private fun onBindWalkViewHolder(holder: WalkSectionViewHolder, section: Section) {
        holder.departureStationName.text = section.departureLocation.name
        holder.departureTime.text = TimeDateUtils.formatTime(section.departureDate)
    }


    override fun getItemViewType(position: Int): Int {
        // TODO: implement walks
        return JOURNEY_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        when (holder.itemViewType) {
            JOURNEY_VIEW_TYPE -> onBindJourneyViewHolder(holder as JourneyViewHolder, section)
            WALK_VIEW_TYPE -> onBindWalkViewHolder(holder as WalkSectionViewHolder, section)
        }
    }


    override fun getItemCount(): Int = sections.size

    interface OnSectionClickedListener {
        fun onSectionClicked(section: Section)
    }

    open class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}