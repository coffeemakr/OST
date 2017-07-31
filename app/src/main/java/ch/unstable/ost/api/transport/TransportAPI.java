package ch.unstable.ost.api.transport;


import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.ConnectionQuery;
import ch.unstable.ost.api.transport.model.Coordinates;
import ch.unstable.ost.api.transport.model.OSLocation;
import ch.unstable.ost.api.transport.model.LocationTypeFilter;
import ch.unstable.ost.api.transport.types.EmptyNumberTypeAdapter;

public class TransportAPI extends BaseHttpJsonAPI {

    private static final Uri BASE_URL = Uri.parse("https://transport.opendata.ch/v1/");
    private static final String TAG = "TransportAPI";

    public TransportAPI() {
    }

    @Override
    protected void onBuildGsonCreated(GsonBuilder gsonBuilder) {
        final TypeAdapter<Number> numberTypeAdapter = new EmptyNumberTypeAdapter();
        gsonBuilder.registerTypeAdapter(Integer.class, numberTypeAdapter);
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

    public List<Connection> getConnections(ConnectionQuery connectionQuery) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("connections")
                .appendQueryParameter("from", connectionQuery.getFrom())
                .appendQueryParameter("to", connectionQuery.getTo());
        if (connectionQuery.hasVia()) {
            for (String via : connectionQuery.getVia()) {
                builder.appendQueryParameter("via[]", via);
            }
        }

        if(connectionQuery.getStarTime() != null) {
            addURLDate(builder, connectionQuery.getStarTime());
        }
        return loadConnections(builder);
    }

    private List<Connection> loadConnections(Uri.Builder builder) throws IOException {
        return loadJson(builder, ConnectionList.class).connections;
    }

    private List<OSLocation> loadLocations(Uri.Builder builder) throws IOException {
        return loadJson(builder, StationsList.class).stations;
    }


    public List<OSLocation> getLocationsByQuery(String query) throws IOException {
        return getLocationsByQuery(query, null);
    }

    public List<OSLocation> getLocationsByQuery(String query, @Nullable LocationTypeFilter typeFilter) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("locations")
                .appendQueryParameter("query", query);
        if(typeFilter != null) {
            builder.appendQueryParameter("type", typeFilter.getIdentifier());
        }
        return loadLocations(builder);
    }

    public List<OSLocation> getLocationsByPosition(Coordinates coordinates) throws IOException {
        return getLocationsByPosition(coordinates, null, null);
    }

    public List<OSLocation> getLocationsByPosition(Coordinates coordinates,
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
        List<OSLocation> stations;
    }

    private static class ConnectionList {
        @SerializedName("connections")
        List<Connection> connections;
    }

}
