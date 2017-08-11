package ch.unstable.ost.api;


import android.support.annotation.Nullable;

import java.io.IOException;

public interface StationsDAO {
    ch.unstable.ost.api.model.impl.Location[] getStationsByQuery(String query) throws IOException;

    ch.unstable.ost.api.model.impl.Location[] getStationsByQuery(String query, @Nullable ch.unstable.ost.api.model.impl.Location.StationType types[]) throws IOException;
}
