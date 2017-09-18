package ch.unstable.ost.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ch.unstable.ost.database.model.CachedConnection;
import ch.unstable.ost.database.model.QueryHistory;

@Database(entities = {CachedConnection.class, QueryHistory.class}, version = 1)
@TypeConverters(value = {ConnectionConverters.class})
public abstract class CacheDatabase extends RoomDatabase {
    public abstract CachedConnectionDAO cachedConnectionDao();

    public abstract QueryHistoryDao queryHistoryDao();
}
