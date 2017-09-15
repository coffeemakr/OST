package ch.unstable.ost.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import ch.unstable.ost.database.model.CachedConnection;
import io.reactivex.Flowable;

@Dao
public abstract class CachedConnectionDAO {

    @Query("SELECT * FROM " + CachedConnection.TABLE_NAME + " WHERE id = :id")
    abstract public Flowable<CachedConnection> getConnectionById(long id);

    @Query("SELECT * FROM " + CachedConnection.TABLE_NAME + " ORDER BY creationDate")
    abstract public Flowable<CachedConnection> getConnections();

    @Insert
    abstract public void addConnection(CachedConnection connection);
}
