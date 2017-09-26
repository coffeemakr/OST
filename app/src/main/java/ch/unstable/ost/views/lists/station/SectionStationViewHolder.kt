package ch.unstable.ost.views.lists.station

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import ch.unstable.ost.R
import ch.unstable.ost.views.StopDotView

class SectionStationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * The name of the passing station
     */
    val stationName: TextView = itemView.findViewById(R.id.stationName)
    /**
     * The time of the stop (arrival and departure)
     */
    val stationTime: TextView = itemView.findViewById(R.id.stationTime)
    /**
     * The stop-dot-view
     */
    val stopDotView: StopDotView = itemView.findViewById(R.id.stopDotView)

}
