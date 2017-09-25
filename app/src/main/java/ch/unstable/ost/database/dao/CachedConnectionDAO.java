package ch.unstable.ost.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.database.model.CachedConnection;
import io.reactivex.Flowable;

@Dao
public abstract class CachedConnectionDAO {

    @Query("SELECT * FROM " + CachedConnection.TABLE_NAME + " WHERE query_id = :queryId ORDER BY sequence ASC")
    public abstract Flowable<List<CachedConnection>> getCachedConnectionsForQueryId(long queryId);

    @Insert
    public abstract void addCachedConnections(CachedConnection cachedConnection);

}
