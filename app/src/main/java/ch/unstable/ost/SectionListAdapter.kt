package ch.unstable.ost

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.SectionListAdapter.SectionViewHolder
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.api.model.SectionType
import ch.unstable.ost.databinding.ItemConnectionSectionJourneyBinding
import ch.unstable.ost.databinding.ItemConnectionSectionWalkBinding
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
                val binding = ItemConnectionSectionJourneyBinding.inflate(inflater, parent, false)
                return JourneyViewHolder(binding)
            }
            WALK_VIEW_TYPE -> {
                val binding = ItemConnectionSectionWalkBinding.inflate(inflater, parent, false)
                return WalkSectionViewHolder(binding)
            }
        }
        throw IllegalStateException("unknown viewType: $viewType")
    }

    private fun onBindJourneyViewHolder(holder: JourneyViewHolder, section: Section) {
        with(holder.binding) {
            section.realtimeInfo?.let { realtimeInfo ->
                if (realtimeInfo.arrival.actualTime != section.arrival.time) {
                    arrivalTime.paintFlags = arrivalTime.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    actualArrivalTime.visibility = View.VISIBLE
                    actualArrivalTime.text = TimeDateUtils.formatTime(realtimeInfo.arrival.actualTime)
                } else {
                    actualArrivalTime.visibility = View.GONE
                    arrivalTime.paintFlags = arrivalTime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                if (realtimeInfo.departure.actualTime != section.departure.time) {
                    departureTime.paintFlags = departureTime.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    departureActual.visibility = View.VISIBLE
                    departureActual.text = TimeDateUtils.formatTime(realtimeInfo.departure.actualTime)
                } else {
                    departureActual.visibility = View.GONE
                    departureTime.paintFlags = departureTime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            arrivalStationName.text = section.arrival.station.name
            departureStationName.text = section.departure.station.name
            arrivalTime.text = TimeDateUtils.formatTime(section.arrival.time)
            departureTime.text = TimeDateUtils.formatTime(section.departure.time)
            productName.text = section.transportInfo?.shortDisplayName
            endDestination.text = section.arrival.station.name
            departurePlatform.text = section.departure.platform
            arrivalPlatform.text = section.arrival.platform

            holder.itemView.tag = section
            holder.itemView.setOnClickListener(onJourneyItemClickListener)
        }
    }

    private fun onBindWalkViewHolder(holder: WalkSectionViewHolder, section: Section) {
        with(holder.binding) {
            departureStationName.text = section.departure.station.name
            departureTime.text = TimeDateUtils.formatDuration(
                    root.context.resources,
                    section.departure.time,
                    section.arrival.time
            )
        }
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

    class WalkSectionViewHolder(val binding: ItemConnectionSectionWalkBinding) : SectionViewHolder(binding.root)

    class JourneyViewHolder(val binding: ItemConnectionSectionJourneyBinding) : SectionViewHolder(binding.root)

    open class SectionViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    companion object {
        const val TAG = "SectionListAdapters"
        private const val JOURNEY_VIEW_TYPE = 1
        private const val WALK_VIEW_TYPE = 2
    }
}