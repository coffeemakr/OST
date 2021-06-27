package ch.unstable.ost

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.SectionListAdapter.SectionViewHolder
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.api.model.SectionType
import ch.unstable.ost.utils.TimeDateUtils

internal class SectionListAdapter : RecyclerView.Adapter<SectionViewHolder>() {
    private var sections = listOf<Section>()
    private var onJourneyClickedListener: OnSectionClickedListener? = null
    private val onJourneyItemClickListener = View.OnClickListener { v ->
        val section = v.tag as Section?
        if (section == null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Got tag null for view: $v")
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
        throw IllegalStateException("unknown viewType: $viewType")
    }

    private fun onBindJourneyViewHolder(holder: JourneyViewHolder, section: Section?) {
        holder.arrivalStationName.text = section!!.arrival.station.name
        holder.departureStationName.text = section.departure.station.name
        holder.arrivalTime.text = TimeDateUtils.formatTime(section.arrival.time)
        holder.departureTime.text = TimeDateUtils.formatTime(section.departure.time)
        holder.productName.text = section.transportInfo?.displayName
        holder.endDestination.text = section.arrival.station.name
        holder.departurePlatform.text = section.departure.platform
        holder.arrivalPlatform.text = section.arrival.platform
        holder.itemView.tag = section
        holder.itemView.setOnClickListener(onJourneyItemClickListener)
    }

    private fun onBindWalkViewHolder(holder: WalkSectionViewHolder, section: Section) {
        holder.departureStationName.text = section.departure.station.name
        holder.departureTime.text = TimeDateUtils.formatDuration(
                holder.itemView.context.resources,
                section.departure.time,
                section.arrival.time
        )
    }

    override fun getItemViewType(position: Int): Int {
        val section = sections[position]
        return when(section.type) {
            SectionType.TRANSPORT -> JOURNEY_VIEW_TYPE
            SectionType.WALK -> WALK_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        when (holder.itemViewType) {
            JOURNEY_VIEW_TYPE -> onBindJourneyViewHolder(holder as JourneyViewHolder, section)
            WALK_VIEW_TYPE -> onBindWalkViewHolder(holder as WalkSectionViewHolder, section)
        }
    }

    fun setSections(sections: List<Section>) {
        this.sections = sections
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    fun setOnJourneyClickedListener(onJourneyClickedListener: OnSectionClickedListener?) {
        this.onJourneyClickedListener = onJourneyClickedListener
    }

    interface OnSectionClickedListener {
        fun onSectionClicked(section: Section)
    }

    class WalkSectionViewHolder(itemView: View) : SectionViewHolder(itemView) {
        val departureTime: TextView = itemView.findViewById(R.id.departureTime)
        val departureStationName: TextView = itemView.findViewById(R.id.departureStationName)

    }

    class JourneyViewHolder(itemView: View) : SectionViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val endDestination: TextView = itemView.findViewById(R.id.endDestination)
        val departurePlatform: TextView = itemView.findViewById(R.id.departurePlatform)
        val arrivalPlatform: TextView = itemView.findViewById(R.id.arrivalPlatform)
        val arrivalStationName: TextView = itemView.findViewById(R.id.arrivalStationName)
        val departureStationName: TextView = itemView.findViewById(R.id.departureStationName)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrivalTime)
        val departureTime: TextView = itemView.findViewById(R.id.departureTime)
    }

    open class SectionViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    companion object {
        const val TAG = "SectionListAdapters"
        private const val JOURNEY_VIEW_TYPE = 1
        private const val WALK_VIEW_TYPE = 2
    }
}