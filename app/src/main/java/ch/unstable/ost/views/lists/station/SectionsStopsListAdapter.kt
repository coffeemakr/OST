package ch.unstable.ost.views.lists.station

import android.view.View
import ch.unstable.ost.R
import ch.unstable.ost.api.model.PassingCheckpoint
import ch.unstable.ost.utils.TimeDateUtils
import ch.unstable.ost.views.StopDotView
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter


class SectionsStopsListAdapter : SingleTypeSimplerAdapter<PassingCheckpoint, SectionStationViewHolder>() {

    override val layout: Int
        get() = R.layout.item_connection_journey_station


    override fun onBindViewHolder(viewHolder: SectionStationViewHolder, element: PassingCheckpoint, position: Int) {
        with(viewHolder) {
            stationName.text = element.station.name
            when (position) {
                0 -> stopDotView.lineMode = StopDotView.LineMode.TOP
                itemCount - 1 -> stopDotView.lineMode = StopDotView.LineMode.BOTTOM
                else -> stopDotView.lineMode = StopDotView.LineMode.BOTH
            }
            TimeDateUtils.setStationStay(stationTime, element.arrivalTime, element.departureTime)
        }
    }

    override fun onCreateViewHolder(itemView: View) = SectionStationViewHolder(itemView)

}
