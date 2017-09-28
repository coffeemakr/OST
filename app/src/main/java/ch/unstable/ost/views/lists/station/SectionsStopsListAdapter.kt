package ch.unstable.ost.views.lists.station

import android.view.View

import ch.unstable.ost.R
import ch.unstable.ost.api.model.PassingCheckpoint
import ch.unstable.ost.utils.TimeDateUtils
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter
import ch.unstable.ost.views.StopDotView


class SectionsStopsListAdapter : SingleTypeSimplerAdapter<PassingCheckpoint, SectionStationViewHolder>() {

    override val layout: Int
        get() = R.layout.item_connection_journey_station


    override fun onBindViewHolder(viewHolder: SectionStationViewHolder, element: PassingCheckpoint, position: Int) {
        with(viewHolder) {
            stationName.text = element.stationName
            if (position == 0) {
                stopDotView.setLineMode(StopDotView.Type.TOP)
            } else if (position == itemCount - 1) {
                stopDotView.setLineMode(StopDotView.Type.BOTTOM)
            } else {
                stopDotView.setLineMode(StopDotView.Type.BOTH)
            }
            TimeDateUtils.setStationStay(stationTime, element.arrivalTime, element.departureTime)
        }
    }

    override fun onCreateViewHolder(itemView: View) = SectionStationViewHolder(itemView)

}
