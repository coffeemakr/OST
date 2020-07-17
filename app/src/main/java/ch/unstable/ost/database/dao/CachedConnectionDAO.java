package ch.unstable.ost.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.unstable.ost.database.model.CachedConnection;
import io.reactivex.Flowable;

@Dao
public abstract class CachedConnectionDAO {

    @Query("SELECT * FROM " + CachedConnection.TABLE_NAME + " WHERE query_id = :queryId ORDER BY sequence ASC")
    public abstract Flowable<List<CachedConnection>> getCachedConnectionsForQueryId(long queryId);

    @Insert
    public abstract void addCachedConnections(CachedConnection cachedConnection);

}
