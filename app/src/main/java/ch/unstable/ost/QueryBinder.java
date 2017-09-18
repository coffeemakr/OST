package ch.unstable.ost;


import android.arch.persistence.room.Query;
import android.content.Context;
import android.support.annotation.MainThread;
import android.widget.TextView;

import java.util.Date;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.model.QueryHistory;
import ch.unstable.ost.utils.LocalizationUtils;

public class QueryBinder {

    @MainThread
    public static void bindDate(QueryHistory queryEntry, TextView dateView, TextView fromAndTo) {
        final Context context = dateView.getContext();
        final ConnectionQuery query = queryEntry.getQuery();
        Date arrival = query.getArrivalTime();
        Date departure = query.getDepartureTime();
        if (query.isNow()) {
            // The search was for "now" so we show the time the query was submitted
            departure = queryEntry.getCreationDate();
        }
        dateView.setText(LocalizationUtils.getArrivalOrDepartureText(context, arrival, departure));
        fromAndTo.setText(context.getString(R.string.fromAndTo, query.getFrom(), query.getTo()));

    }
}
