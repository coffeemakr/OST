package ch.unstable.ost.views.lists.section

import android.view.View
import android.widget.TextView
import ch.unstable.ost.R

class WalkSectionViewHolder(itemView: View) : SectionListAdapter.SectionViewHolder(itemView) {
    val departureTime: TextView = itemView.findViewById(R.id.departureTime)
    val departureStationName: TextView = itemView.findViewById(R.id.departureStationName)
}