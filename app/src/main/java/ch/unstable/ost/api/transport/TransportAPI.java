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

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.model.impl.ArrivalCheckpoint;
import ch.unstable.ost.api.model.impl.Connection;
import ch.unstable.ost.api.model.impl.DepartureCheckpoint;
import ch.unstable.ost.api.model.impl.Location;
import ch.unstable.ost.api.model.impl.PassingCheckpoint;
import ch.unstable.ost.api.model.impl.Section;
import ch.unstable.ost.api.transport.types.ArrivalStopDeserializer;
import ch.unstable.ost.api.transport.types.ConnectionDeserializer;
import ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer;
import ch.unstable.ost.api.transport.types.LocationDeserializer;
import ch.unstable.ost.api.transport.types.PassingCheckpointDeserializer;
import ch.unstable.ost.api.transport.types.SectionListDeserializer;
import io.mikael.urlbuilder.UrlBuilder;

public class TransportAPI extends BaseHttpJsonAPI implements StationsDAO {

    private static final String BASE_URL = "https://transport.opendata.ch/v1/";
    private static final String LOCATION_URL = BASE_URL + "locations";
    private static final String CONNECTIONS_URL = BASE_URL + "connections";

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

    private static void addURLDate(UrlBuilder uriBuilder, Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        uriBuilder
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

    public Connection[] getConnections(ConnectionQuery connectionQuery) throws IOException {
        UrlBuilder builder = UrlBuilder.fromString(CONNECTIONS_URL)
                .addParameter("from", connectionQuery.getFrom())
                .addParameter("to", connectionQuery.getTo());
        if (connectionQuery.hasVia()) {
            for (String via : connectionQuery.getVia()) {
                builder.addParameter("via[]", via);
            }
        }

        if (connectionQuery.getDepartureTime() != null) {
            addURLDate(builder, connectionQuery.getDepartureTime());
        }
        return loadConnections(builder.toUrl());
    }

    private Connection[] loadConnections(URL builder) throws IOException {
        return loadJson(builder, ConnectionList.class).connections;
    }

    private Location[] loadLocations(URL builder) throws IOException {
        return loadJson(builder, StationsList.class).stations;
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
                        builder.addParameter("type", "station");
                        break;
                    case POI:
                        builder.addParameter("type", "poi");
                        break;
                    case ADDRESS:
                        builder.addParameter("type", "address");
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
