package ch.unstable.ost.views.lists.query

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import ch.unstable.ost.R

/**
 * View holder for a query history entry
 */
open class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * Text field containing the description of the time restriction
     * e.g. "Departure 11:20"
     */
    val date: TextView = itemView.findViewById(R.id.date_text)
    /**
     * Text field containing the description of the route.
     * e.g "From Zürich to Basel SBB"
     */
    val fromAndTo: TextView = itemView.findViewById(R.id.from_to_text)

}
