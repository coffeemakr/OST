package ch.unstable.ost.api.transport;


import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.ConnectionQuery;
import ch.unstable.ost.api.transport.model.Coordinates;
import ch.unstable.ost.api.transport.model.Location;
import ch.unstable.ost.api.transport.model.LocationTypeFilter;
import ch.unstable.ost.api.transport.types.EmptyNumberTypeAdapter;

public class TransportAPI {

    private static final String USER_AGENT = "OST/0.1";
    private static final Uri BASE_URL = Uri.parse("https://transport.opendata.ch/v1/");
    private static final String TAG = "TransportAPI";
    private final Gson gson;

    public TransportAPI() {
        final TypeAdapter<Number> numberTypeAdapter = new EmptyNumberTypeAdapter();
        gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, numberTypeAdapter)
                .create();
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

    private <T> T loadJson(Uri.Builder builder, Class<T> jsonClass) throws IOException {
        URL url = new URL(builder.build().toString());
        Log.d(TAG, "loading JSON " + url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        try {
            return gson.fromJson(inputStreamReader, jsonClass);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private List<Connection> loadConnections(Uri.Builder builder) throws IOException {
        return loadJson(builder, ConnectionList.class).connections;
    }

    private List<Location> loadLocations(Uri.Builder builder) throws IOException {
        return loadJson(builder, StationsList.class).stations;
    }

    public List<Location> getLocationsByQuery(String query) throws IOException {
        Uri.Builder builder = BASE_URL.buildUpon()
                .appendPath("locations")
                .appendQueryParameter("query", query);
        return loadLocations(builder);
    }

    public List<Location> getLocationsByPosition(Coordinates coordinates) throws IOException {
        return getLocationsByPosition(coordinates, null, null);
    }

    public List<Location> getLocationsByPosition(Coordinates coordinates,
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
        List<Location> stations;
    }

    private static class ConnectionList {
        @SerializedName("connections")
        List<Connection> connections;
    }

}
