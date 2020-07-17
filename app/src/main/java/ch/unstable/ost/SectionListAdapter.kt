package ch.unstable.ost

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.SectionListAdapter.SectionViewHolder
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.utils.TimeDateUtils

internal class SectionListAdapter : RecyclerView.Adapter<SectionViewHolder>() {
    private var sections = arrayOfNulls<Section>(0)
    private var onJourneyClickedListener: OnSectionClickedListener? = null
    private val onJourneyItemClickListener = View.OnClickListener { v ->
        val section = v.tag as Section
        if (section == null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Got tag null for view: $v")
            return@OnClickListener
        }
        if (onJourneyClickedListener != null) {
            onJourneyClickedListener!!.onSectionClicked(section)
        }
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

    fun onBindJourneyViewHolder(holder: JourneyViewHolder, section: Section?) {
        holder.arrivalStationName.text = section!!.arrivalLocation.name
        holder.departureStationName.text = section.departureLocation.name
        holder.arrivalTime.text = TimeDateUtils.formatTime(section.arrivalDate)
        holder.departureTime.text = TimeDateUtils.formatTime(section.departureDate)
        holder.productName.text = section.lineShortName
        holder.endDestination.text = section.headsign
        holder.departurePlatform.text = section.departurePlatform
        holder.arrivalPlatform.text = section.arrivalPlatform
        holder.itemView.tag = section
        holder.itemView.setOnClickListener(onJourneyItemClickListener)
    }

    private fun onBindWalkViewHolder(holder: WalkSectionViewHolder, section: Section?) {
        holder.departureStationName.text = section!!.departureLocation.name
        holder.departureTime.text = TimeDateUtils.formatTime(section.departureDate)
    }

    override fun getItemViewType(position: Int): Int {
        return JOURNEY_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        when (holder.itemViewType) {
            JOURNEY_VIEW_TYPE -> onBindJourneyViewHolder(holder as JourneyViewHolder, section)
            WALK_VIEW_TYPE -> onBindWalkViewHolder(holder as WalkSectionViewHolder, section)
        }
    }

    fun setSections(sections: List<Section?>) {
        this.sections = sections.toTypedArray()
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
        val departureTime: TextView
        val departureStationName: TextView

        init {
            departureStationName = itemView.findViewById(R.id.departureStationName)
            departureTime = itemView.findViewById(R.id.departureTime)
        }
    }

    class JourneyViewHolder(itemView: View) : SectionViewHolder(itemView) {
        val productName: TextView
        val endDestination: TextView
        val departurePlatform: TextView
        val arrivalPlatform: TextView
        val arrivalStationName: TextView
        val departureStationName: TextView
        val arrivalTime: TextView
        val departureTime: TextView

        init {
            productName = itemView.findViewById(R.id.productName)
            endDestination = itemView.findViewById(R.id.endDestination)
            departurePlatform = itemView.findViewById(R.id.departurePlatform)
            arrivalPlatform = itemView.findViewById(R.id.arrivalPlatform)
            arrivalStationName = itemView.findViewById(R.id.arrivalStationName)
            arrivalTime = itemView.findViewById(R.id.arrivalTime)
            departureStationName = itemView.findViewById(R.id.departureStationName)
            departureTime = itemView.findViewById(R.id.departureTime)
        }
    }

    open class SectionViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    companion object {
        const val TAG = "SectionListAdapters"
        private const val JOURNEY_VIEW_TYPE = 1
        private const val WALK_VIEW_TYPE = 2
    }
}