package ch.unstable.ost.api.transport;


import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.api.transport.types.ArrivalStopDeserializer;
import ch.unstable.ost.api.transport.types.ConnectionDeserializer;
import ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer;
import ch.unstable.ost.api.transport.types.LocationDeserializer;
import ch.unstable.ost.api.transport.types.PassingCheckpointDeserializer;
import ch.unstable.ost.api.transport.types.SectionListDeserializer;
import io.mikael.urlbuilder.UrlBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransportAPI extends BaseHttpJsonAPI implements StationsDAO {

    private static final String BASE_URL = "https://transport.opendata.ch/v1/";
    private static final String LOCATION_URL = BASE_URL + "locations";
    private static final String CONNECTIONS_URL = BASE_URL + "connections";

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Berlin");

    private static final String TAG = "TransportAPI";
    /**
     * From the documentation:
     * <blockquote>
     * 0 - 3. Allows pagination of connections. Zero-based,
     * so first page is 0, second is 1, third is 2 and so on.
     * </blockquote>
     */
    private static final String URL_PARAMETER_PAGE = "page";

    public TransportAPI() {
    }

    private static void addTransportationFilter(Uri.Builder builder, Transportation[] transportations) {
        if (transportations != null) {
            for (Transportation transportation : transportations) {
                builder.appendQueryParameter("transportations[]", transportation.getIdentifier());
            }
        }
    }

    private static UrlBuilder addURLDate(UrlBuilder uriBuilder, Date date) {
        checkNotNull(uriBuilder, "uriBuilder");
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
        //final TypeAdapter<Number> numberTypeAdapter = new EmptyNumberTypeAdapter();
        //gsonBuilder.registerTypeAdapter(Integer.class, numberTypeAdapter);
        gsonBuilder.registerTypeAdapter(Location.class, LocationDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(ArrivalCheckpoint.class, ArrivalStopDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(DepartureCheckpoint.class, DepartureCheckpointDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(PassingCheckpoint.class, PassingCheckpointDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(Section[].class, SectionListDeserializer.INSTANCE);
        gsonBuilder.registerTypeAdapter(Connection.class, ConnectionDeserializer.INSTANCE);
    }

    public Connection[] getConnections(ConnectionQuery connectionQuery, int page) throws IOException {
        UrlBuilder builder = UrlBuilder.fromString(CONNECTIONS_URL)
                .addParameter("from", connectionQuery.getFrom())
                .addParameter("to", connectionQuery.getTo())
                .addParameter("limit", "6")
                .addParameter(URL_PARAMETER_PAGE, Integer.toString(page));
        if (connectionQuery.hasVia()) {
            for (String via : connectionQuery.getVia()) {
                builder = builder.addParameter("via[]", via);
            }
        }

        if (connectionQuery.getDepartureTime() != null) {
            builder = addURLDate(builder, connectionQuery.getDepartureTime());
        } else if (connectionQuery.getArrivalTime() != null) {
            builder = builder.setParameter("isArrivalTime", "1");
            builder = addURLDate(builder, connectionQuery.getArrivalTime());
        }
        return loadConnections(builder.toUrl());
    }

    private Connection[] loadConnections(URL url) throws IOException {
        return loadJson(url, ConnectionList.class).connections;
    }

    private Location[] loadLocations(URL url) throws IOException {
        return loadJson(url, StationsList.class).stations;
    }


    @Override
    public Location[] getStationsByQuery(String query) throws IOException {
        return getStationsByQuery(query, null);
    }

    @Override
    public Location[] getStationsByQuery(String query, @Nullable Location.StationType[] types) throws IOException {
        UrlBuilder builder = UrlBuilder.fromString(LOCATION_URL)
                .addParameter("query", query);
        if (types != null && types.length > 0) {
            for (Location.StationType type : types) {
                switch (type) {
                    case TRAIN:
                    case BUS:
                    case TRAM:
                        builder = builder.addParameter("type", "station");
                        break;
                    case POI:
                        builder = builder.addParameter("type", "poi");
                        break;
                    case ADDRESS:
                        builder = builder.addParameter("type", "address");
                        break;
                    case UNKNOWN:
                    default:
                        throw new IllegalArgumentException(String.format("%s is not a valid filter", type));
                }
            }
        }
        return loadLocations(builder.toUrl());
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
