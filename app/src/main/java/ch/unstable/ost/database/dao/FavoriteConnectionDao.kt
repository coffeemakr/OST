package ch.unstable.ost.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ch.unstable.ost.database.model.FavoriteConnection
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FavoriteConnectionDao {
    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " ORDER BY creation_date DESC LIMIT 100")
    fun getFavoriteConnections(): Flowable<List<FavoriteConnection>>

    @Insert
    fun addConnection(connections: FavoriteConnection): Long

    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " ORDER BY creation_date DESC LIMIT 1")
    fun getLatestFavorite(): Single<FavoriteConnection>

    @Delete
    fun removeConnectionById(id: FavoriteConnection)

    @Query("SELECT * FROM " + FavoriteConnection.TABLE_NAME + " WHERE id = :id")
    fun getFavoriteById(id: Long): Single<FavoriteConnection>
}