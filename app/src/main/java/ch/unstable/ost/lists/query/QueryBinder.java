package ch.unstable.ost.lists.query;


import android.content.Context;
import android.support.annotation.MainThread;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import java.util.Date;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.model.QueryHistory;
import ch.unstable.ost.utils.LocalizationUtils;

public class QueryBinder {

    @MainThread
    public static void bindQuery(QueryHistory queryEntry, TextView dateView, TextView fromAndTo) {
        final Context context = dateView.getContext();
        final ConnectionQuery query = queryEntry.getQuery();
        Date arrival = query.getArrivalTime();
        Date departure = query.getDepartureTime();
        if (query.isNow()) {
            // The search was for "now" so we show the time the query was submitted
            departure = queryEntry.getCreationDate();
        }
        dateView.setText(LocalizationUtils.getArrivalOrDepartureText(context, arrival, departure));
        bindFromToText(fromAndTo, query);
    }

    public static void bindFromToText(TextView textView, ConnectionQuery query) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(query, "query is null");
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(textView, "textView is null");
        textView.setText(textView.getContext().getString(R.string.fromAndTo, query.getFrom(), query.getTo()));
    }

    public static void bindFromToText(TextView textView, Connection connection) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(connection, "connection is null");
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(textView, "textView is null");
        textView.setText(textView.getContext().getString(R.string.fromAndTo, connection.getDepartureName(), connection.getArrivalName()));
    }
}
