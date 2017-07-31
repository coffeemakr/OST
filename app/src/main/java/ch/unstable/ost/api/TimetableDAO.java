package ch.unstable.ost.api;


import android.support.annotation.Nullable;

import java.io.IOException;

import ch.unstable.ost.api.model.Station;
import ch.unstable.ost.api.transport.model.LocationTypeFilter;

public interface TimetableDAO {
    Station[] getStationsByQuery(String query) throws IOException;

    Station[] getStationsByQuery(String query, @Nullable Station.StationType types[]) throws IOException;
}
