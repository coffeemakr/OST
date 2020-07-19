package ch.unstable.ost.views.lists.connection

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.R
import ch.unstable.ost.views.ConnectionLineView

/**
 * View holder for connection items
 */
open class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val differentStartStation: TextView = itemView.findViewById(R.id.differentDepartureStation)
    val startTime: TextView = itemView.findViewById(R.id.startTime)
    val endTime: TextView = itemView.findViewById(R.id.endTime)
    val direction: TextView = itemView.findViewById(R.id.direction)
    val connectionLineView: ConnectionLineView = itemView.findViewById(R.id.connectionLineView)
    val firstTransportName: TextView = itemView.findViewById(R.id.firstTransportName)
    val duration: TextView = itemView.findViewById(R.id.duration)
    val platform: TextView = itemView.findViewById(R.id.platform)

}
