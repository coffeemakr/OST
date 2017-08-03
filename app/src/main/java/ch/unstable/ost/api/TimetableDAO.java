package ch.unstable.ost.api;


import android.support.annotation.Nullable;

import java.io.IOException;

import ch.unstable.ost.api.model.Location;

public interface TimetableDAO {
    Location[] getStationsByQuery(String query) throws IOException;

    Location[] getStationsByQuery(String query, @Nullable Location.StationType types[]) throws IOException;
}
