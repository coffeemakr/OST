package ch.unstable.ost.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.unstable.ost.database.model.QueryHistory;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public abstract class QueryHistoryDao {

    @Query("SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC")
    abstract public Flowable<List<QueryHistory>> getConnections();

    @Insert
    abstract public long addConnection(QueryHistory connection);

    @Query("SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC LIMIT 1")
    public abstract Single<QueryHistory> getLatestQuery();


    @Query(value = "SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC")
    abstract List<QueryHistory> getFavoriteQueries();
}
