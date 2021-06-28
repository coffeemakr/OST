package ch.unstable.ost.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ch.unstable.ost.api.model.ConnectionQuery
import com.google.common.base.Preconditions
import java.util.*

@Entity(tableName = QueryHistory.TABLE_NAME)
class QueryHistory {
    @PrimaryKey(autoGenerate = true)
    val id: Long
    val creationDate: Date
    val query: ConnectionQuery

    constructor(id: Long, creationDate: Date, query: ConnectionQuery) {
        this.id = id
        this.creationDate = Preconditions.checkNotNull(creationDate, "creationDate")
        this.query = Preconditions.checkNotNull(query, "query")
    }

    @Ignore
    constructor(query: ConnectionQuery) {
        id = 0
        creationDate = Date()
        this.query = Preconditions.checkNotNull(query, "query")
    }

    companion object {
        const val TABLE_NAME = "queries"
    }
}