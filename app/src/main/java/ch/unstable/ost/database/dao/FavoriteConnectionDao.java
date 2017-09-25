package ch.unstable.ost.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ch.unstable.ost.database.model.FavoriteConnection;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface FavoriteConnectionDao {
    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " ORDER BY creation_date DESC LIMIT 100")
    Flowable<List<FavoriteConnection>> getFavoriteConnections();

    @Insert
    long addConnection(FavoriteConnection connections);

    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " ORDER BY creation_date DESC LIMIT 1")
    Single<FavoriteConnection> getLatestFavorite();

    @Delete
    void removeConnectionById(FavoriteConnection id);

    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " WHERE id = :id")
    Single<FavoriteConnection> getFavoriteById(long id);
}
