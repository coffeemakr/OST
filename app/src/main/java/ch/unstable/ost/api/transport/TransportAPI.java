package ch.unstable.ost.api.transport;


import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.unstable.ost.api.TimetableDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.transport.model.Coordinates;
import ch.unstable.ost.api.transport.model.LocationTypeFilter;
import ch.unstable.ost.api.transport.model.Location;
import ch.unstable.ost.api.transport.types.EmptyNumberTypeAdapter;

public class TransportAPI extends BaseHttpJsonAPI implements TimetableDAO {

    private static final Uri BASE_URL = Uri.parse("https://transport.opendata.ch/v1/");
    private static final String TAG = "TransportAPI";

    public TransportAPI() {
    }

    private static void addTransportationFilter(Uri.Builder builder, Transportation[] transportations) {
        if (transportations != null) {
            for (Transportation transportation : transportations) {
                builder.appendQueryParameter("transportations[]", transportation.getIdentifier());
            }
        }
    }

    private static void addURLDate(Uri.Builder uriBuilder, Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        uriBuilder
                .appendQueryParameter("time", timeFormat.format(date))
                .appendQueryParameter("date", dateFormat.format(date));
    }

    @Override
    protected void onBuildGsonCreated(GsonBuilder gsonBuilder) {
        final TypeAdapter<Number> numberTypeAdapter = new EmptyNumberTypeAdapter();
        gsonBuilder.registerTypeAdapter(Integer.class, numberTypeAdapter);
    }

    public Connection[] getConnections(ConnectionQuery connectionQuery) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("connections")
                .appendQueryParameter("from", connectionQuery.getFrom())
                .appendQueryParameter("to", connectionQuery.getTo());
        if (connectionQuery.hasVia()) {
            for (String via : connectionQuery.getVia()) {
                builder.appendQueryParameter("via[]", via);
            }
        }

        if (connectionQuery.getDepartureTime() != null) {
            addURLDate(builder, connectionQuery.getDepartureTime());
        }
        return loadConnections(builder);
    }

    private Connection[] loadConnections(Uri.Builder builder) throws IOException {
        return loadJson(builder, ConnectionList.class).connections;
    }

    private Location[] loadLocations(Uri.Builder builder) throws IOException {
        return loadJson(builder, StationsList.class).stations;
    }



    @Override
    public ch.unstable.ost.api.model.Location[] getStationsByQuery(String query) throws IOException {
        return getStationsByQuery(query, null);
    }

    @Override
    public ch.unstable.ost.api.model.Location[] getStationsByQuery(String query, @Nullable ch.unstable.ost.api.model.Location.StationType[] types) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("locations")
                .appendQueryParameter("query", query);
        if(types != null && types.length > 0) {
            for(ch.unstable.ost.api.model.Location.StationType type: types) {
                switch (type) {
                    case TRAIN:
                    case BUS:
                        builder.appendQueryParameter("type", LocationTypeFilter.STATION.getIdentifier());
                        break;
                    case POI:
                        builder.appendQueryParameter("type", LocationTypeFilter.POI.getIdentifier());
                        break;
                    case ADDRESS:
                        builder.appendQueryParameter("type", LocationTypeFilter.ADDRESS.getIdentifier());
                        break;
                    case UNKNOWN:
                        break;
                }
            }
        }
        return loadLocations(builder);
    }

    public Location[] getLocationsByPosition(Coordinates coordinates) throws IOException {
        return getLocationsByPosition(coordinates, null, null);
    }

    public Location[] getLocationsByPosition(Coordinates coordinates,
                                             @Nullable LocationTypeFilter type,
                                             @Nullable Transportation[] transportationFilter) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("locations")
                .appendQueryParameter("x", String.valueOf(coordinates.x))
                .appendQueryParameter("y", String.valueOf(coordinates.y));
        if (type != null) {
            builder.appendQueryParameter("type", type.getIdentifier());
        }
        addTransportationFilter(builder, transportationFilter);
        return loadLocations(builder);
    }

    private static class StationsList {
        @SerializedName("stations")
        Location[] stations;
    }

    private static class ConnectionList {
        @SerializedName("connections")
        Connection[] connections;
    }

}
