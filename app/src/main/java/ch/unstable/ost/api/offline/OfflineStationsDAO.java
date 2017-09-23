package ch.unstable.ost.api.offline;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.Location.StationType;
import ch.unstable.ost.api.offline.model.LocationEntity;

@Dao
public abstract class OfflineStationsDAO implements StationsDAO {

    private static final String WHERE_QUERY_MATCHES = "id in (SELECT docid FROM fts_stations WHERE fts_stations MATCH :query)";
    private static final String ORDER_BY_DESCENDING_FREQUENCY = " ORDER BY frequency DESC ";

    private static String getFullQuery(String query) {
        StringBuilder fullQuery = new StringBuilder();
        for (String part : query.split("\\s")) {
            if (part.isEmpty()) continue;
            if (fullQuery.length() > 0) {
                fullQuery.append(' ');
            }
            fullQuery.append(part);
            fullQuery.append('*');
        }
        return fullQuery.toString();
    }

    @Override
    public Location[] getStationsByQuery(String query) {
        return getStationsByQuery(getFullQuery(query), 0);
    }

    @Override
    public Location[] getStationsByQuery(String query, @Nullable StationType[] types) {
        return getStationsByQuery(getFullQuery(query), StationType.getMask(types));
    }

    @NonNull
    private Location[] convertLocations(LocationEntity[] entities) {
        Location[] locations = new Location[entities.length];
        for (int i = 0; i < locations.length; ++i) {
            LocationEntity entity = entities[i];
            locations[i] = new Location(entity.getName(), entity.getTypes()[0], entity.getId());
        }
        return locations;
    }

    private Location[] getStationsByQuery(String query, int mask) {
        return convertLocations(getStationEntriesByQuery(query, mask));
    }

    @Query("SELECT * FROM stations WHERE (:typeFilter = 0 OR (:typeFilter & types) > 0)  AND " + WHERE_QUERY_MATCHES + ORDER_BY_DESCENDING_FREQUENCY)
    abstract public LocationEntity[] getStationEntriesByQuery(String query, int typeFilter);


}
