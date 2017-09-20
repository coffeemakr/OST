package ch.unstable.ost.api.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.api.search.types.ConnectionDeserializer;
import ch.unstable.ost.api.search.types.LocationDeserializer;
import ch.unstable.ost.api.search.types.PassingCheckpointsDeserializer;
import ch.unstable.ost.api.search.types.StationTypeDeserializer;
import ch.unstable.ost.api.search.types.SectionsDeserializer;
import ch.unstable.ost.api.transport.ConnectionAPI;
import io.mikael.urlbuilder.UrlBuilder;

import static ch.unstable.ost.api.model.Location.StationType;
import static com.google.common.base.Preconditions.checkNotNull;

public class SearchAPI extends BaseHttpJsonAPI implements StationsDAO, ConnectionAPI {

    private final static String BASE_URI = "https://timetable.search.ch/api/";
    private final static String COMPLETION_URL = BASE_URI + "completion.json";
    private final static String CONNECTIONS_URL = BASE_URI + "route.json";
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Berlin");

    @NonNull
    private static UrlBuilder addURLDate(UrlBuilder uriBuilder, Date date) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(uriBuilder, "uriBuilder");
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(date, "date");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        timeFormat.setTimeZone(TIME_ZONE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        dateFormat.setTimeZone(TIME_ZONE);
        return uriBuilder
                .addParameter("time", timeFormat.format(date))
                .addParameter("date", dateFormat.format(date));
    }

    @Override
    protected void onBuildGsonCreated(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(Section[].class, new SectionsDeserializer());
        gsonBuilder.registerTypeAdapter(Connection.class, new ConnectionDeserializer());
        gsonBuilder.registerTypeAdapter(StationType.class, StationTypeDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(PassingCheckpoint.class, PassingCheckpointsDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(Location.class, LocationDeserializer.INSTANCE);
    }

    @Override
    public Location[] getStationsByQuery(String query) throws IOException {
        return getStationsByQuery(query, null);
    }

    @Override
    @NonNull
    public Location[] getStationsByQuery(String query, @Nullable StationType[] types) throws IOException {
        if (types != null && types.length == 0) return new Location[0];
        UrlBuilder builder = UrlBuilder.fromString(COMPLETION_URL)
                .addParameter("show_ids", "1")
                .addParameter("term", checkNotNull(query));
        Type listType = new TypeToken<ArrayList<Location>>() {
        }.getType();
        ArrayList<Location> locationCompletions = loadJson(builder.toUrl(), listType);
        if (types != null) {
            locationCompletions = filterResults(locationCompletions, types);
        }
        return locationCompletions.toArray(new Location[locationCompletions.size()]);
    }

    private ArrayList<Location> filterResults(ArrayList<Location> completions, final StationType[] filter) {
        final int mask = StationType.getMask(filter);
        ArrayList<Location> filtered = new ArrayList<>(completions.size());
        for (Location completion : completions) {
            if ((completion.getType().bit & mask) > 0) {
                filtered.add(completion);
            }
        }
        return filtered;
    }

    @Override
    public int getPageMax() {
        return 0;
    }

    @Override
    public int getPageMin() {
        return 0;
    }

    @Override
    public Connection[] getConnections(ConnectionQuery connectionQuery, int page) throws IOException {
        UrlBuilder builder = UrlBuilder.fromString(CONNECTIONS_URL)
                .addParameter("from", connectionQuery.getFrom())
                .addParameter("to", connectionQuery.getTo());

        if (connectionQuery.getArrivalTime() != null) {
            builder = addURLDate(builder, connectionQuery.getArrivalTime());
            builder = builder.addParameter("time_type", "arrival");
        } else if (connectionQuery.getDepartureTime() != null) {
            builder = addURLDate(builder, connectionQuery.getDepartureTime());
        }
        ConnectionsList connectionsList = loadJson(builder.toUrl(), ConnectionsList.class);
        return connectionsList.connections;
    }

    private static class ConnectionsList {
        public Connection[] connections;
    }
}
