package ch.unstable.ost.api.offline;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.io.IOException;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.offline.model.LocationEntity;

@Dao
public abstract class OfflineStationsDAO implements StationsDAO {

    private static final String WHERE_QUERY_MATCHES = "id in (SELECT docid FROM fts_stations WHERE fts_stations MATCH :query)";
    private static final String ORDER_BY_DESCENDING_FREQUENCY = " ORDER BY frequency DESC ";
    @Query("SELECT * FROM stations WHERE " + WHERE_QUERY_MATCHES + ORDER_BY_DESCENDING_FREQUENCY)
    @Override
    public abstract LocationEntity[] getStationsByQuery(String query);

    @Override
    public LocationEntity[] getStationsByQuery(String query, @Nullable Location.StationType[] types) {
        return getStationsByQuery(query + "*", Location.StationType.getMask(types));
    }

    @Query("SELECT * FROM stations WHERE (:typeFilter & types) > 0  AND " + WHERE_QUERY_MATCHES + ORDER_BY_DESCENDING_FREQUENCY)
    abstract public LocationEntity[] getStationsByQuery(String query, int typeFilter);


}
