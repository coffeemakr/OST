package ch.unstable.ost.views.lists.favorite

import android.view.View
import android.widget.TextView

import ch.unstable.ost.R
import ch.unstable.ost.views.lists.connection.ConnectionViewHolder


class FavoritesViewHolder(itemView: View) : ConnectionViewHolder(itemView) {
    val fromToText: TextView = itemView.findViewById(R.id.from_to_text)
}
