package ch.unstable.ost.views.lists.section

import android.view.View
import android.widget.TextView
import ch.unstable.ost.R

class JourneyViewHolder(itemView: View) : SectionListAdapter.SectionViewHolder(itemView) {
    val productName: TextView = itemView.findViewById(R.id.productName)
    val endDestination: TextView = itemView.findViewById(R.id.endDestination)
    val departurePlatform: TextView = itemView.findViewById(R.id.departurePlatform)
    val arrivalPlatform: TextView = itemView.findViewById(R.id.arrivalPlatform)
    val arrivalStationName: TextView = itemView.findViewById(R.id.arrivalStationName)
    val departureStationName: TextView = itemView.findViewById(R.id.departureStationName)
    val arrivalTime: TextView = itemView.findViewById(R.id.arrivalTime)
    val departureTime: TextView = itemView.findViewById(R.id.departureTime)
}