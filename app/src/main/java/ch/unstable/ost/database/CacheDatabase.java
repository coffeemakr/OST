package ch.unstable.ost.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import ch.unstable.ost.database.dao.CachedConnectionDAO;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.database.dao.QueryHistoryDao;
import ch.unstable.ost.database.model.CachedConnection;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.database.model.QueryHistory;

@Database(entities = {CachedConnection.class, QueryHistory.class, FavoriteConnection.class}, version = 2)
@TypeConverters(value = {ConnectionConverters.class})
public abstract class CacheDatabase extends RoomDatabase {

    public abstract CachedConnectionDAO cachedConnectionDao();

    public abstract QueryHistoryDao queryHistoryDao();

    public abstract FavoriteConnectionDao favoriteConnectionDao();

}
