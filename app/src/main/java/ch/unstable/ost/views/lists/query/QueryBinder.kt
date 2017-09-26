package ch.unstable.ost.views.lists.query


import android.support.annotation.MainThread
import android.widget.TextView
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.utils.LocalizationUtils

object QueryBinder {

    @MainThread
    fun bindQuery(queryEntry: QueryHistory, dateView: TextView, fromAndTo: TextView) {
        val query = queryEntry.query
        // The search was for "now" so we show the time the query was submitted
        val departure = if (query.isNow) query.departureTime else queryEntry.creationDate
        dateView.text = LocalizationUtils.getArrivalOrDepartureText(dateView.context, query.arrivalTime, departure)
        bindFromToText(fromAndTo, query)
    }

    fun bindFromToText(textView: TextView, query: ConnectionQuery) {
        textView.text = textView.context.getString(R.string.fromAndTo, query.from, query.to)
    }

    fun bindFromToText(textView: TextView, connection: Connection) {
        textView.text = textView.context.getString(R.string.fromAndTo, connection.departureName, connection.arrivalName)
    }
}
