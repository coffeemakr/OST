package ch.unstable.ost.database;



import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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
