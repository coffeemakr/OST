package ch.unstable.ost.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ch.unstable.ost.database.model.QueryHistory
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class QueryHistoryDao {
    @get:Query("SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC")
    abstract val connections: Flowable<List<QueryHistory>>
    @Insert
    abstract fun addConnection(connection: QueryHistory): Long

    @get:Query("SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC LIMIT 1")
    abstract val latestQuery: Single<QueryHistory>

    @get:Query(value = "SELECT * FROM " + QueryHistory.TABLE_NAME + " ORDER BY creationDate DESC")
    abstract val favoriteQueries: List<QueryHistory>
}