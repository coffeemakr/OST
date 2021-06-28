package ch.unstable.ost.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ch.unstable.ost.database.model.CachedConnection
import io.reactivex.Flowable

@Dao
abstract class CachedConnectionDAO {
    @Query("SELECT * FROM " + CachedConnection.TABLE_NAME + " WHERE query_id = :queryId ORDER BY sequence ASC")
    abstract fun getCachedConnectionsForQueryId(queryId: Long): Flowable<List<CachedConnection>>
    @Insert
    abstract fun addCachedConnections(cachedConnection: CachedConnection)
}