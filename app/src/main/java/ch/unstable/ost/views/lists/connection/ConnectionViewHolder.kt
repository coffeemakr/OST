package ch.unstable.ost.views.lists.connection

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import ch.unstable.ost.R
import ch.unstable.ost.views.ConnectionLineView

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Verify.verifyNotNull

/**
 * View holder for connection items
 */
open class ConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(checkNotNull(itemView)) {

    val startTime: TextView = itemView.findViewById(R.id.startTime)
    val endTime: TextView = verifyNotNull(itemView.findViewById(R.id.endTime))
    val firstEndDestination: TextView = verifyNotNull(itemView.findViewById(R.id.firstSectionEndDestination))
    val connectionLineView: ConnectionLineView = verifyNotNull(itemView.findViewById(R.id.connectionLineView))
    val firstTransportName: TextView = verifyNotNull(itemView.findViewById(R.id.firstTransportName))
    val duration: TextView = verifyNotNull(itemView.findViewById(R.id.duration))
    val platform: TextView = verifyNotNull(itemView.findViewById(R.id.platform))

}
