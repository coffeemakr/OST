package ch.unstable.ost.views.lists.favorite

import android.support.annotation.LayoutRes
import android.view.View

import ch.unstable.ost.R
import ch.unstable.ost.database.model.FavoriteConnection
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter
import ch.unstable.ost.views.lists.connection.bindConnection

import ch.unstable.ost.views.lists.query.bindFromToText

class FavoritesAdapter : SingleTypeSimplerAdapter<FavoriteConnection, FavoritesViewHolder>() {

    @LayoutRes
    override val layout: Int = R.layout.item_favorite_connection

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(viewHolder: FavoritesViewHolder, element: FavoriteConnection, position: Int) {
        val connection = element.connection
        bindConnection(connection, viewHolder)
        bindFromToText(viewHolder.fromToText, connection)
    }

    override fun onCreateViewHolder(itemView: View): FavoritesViewHolder {
        return FavoritesViewHolder(itemView)
    }


    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }
}
