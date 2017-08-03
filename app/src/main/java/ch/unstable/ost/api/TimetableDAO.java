package ch.unstable.ost.api;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.transport.model.Connection;

public interface TimetableDAO {
    @NonNull
    Location[] getStationsByQuery(@NonNull String query) throws IOException;

    @NonNull
    Location[] getStationsByQuery(@NonNull String query, @Nullable Location.StationType types[]) throws IOException;

    @NonNull
    ch.unstable.ost.api.model.Connection[] getConnections(@NonNull ConnectionQuery connectionQuery) throws IOException;
}
