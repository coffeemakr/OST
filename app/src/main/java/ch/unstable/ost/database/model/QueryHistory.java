package ch.unstable.ost.database.model;



import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

import ch.unstable.ost.api.model.ConnectionQuery;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity(tableName = QueryHistory.TABLE_NAME)
public class QueryHistory {

    public static final String TABLE_NAME = "queries";

    @PrimaryKey(autoGenerate = true)
    private final long id;

    @NonNull
    private final Date creationDate;

    @NonNull
    private final ConnectionQuery query;

    public QueryHistory(long id, Date creationDate, ConnectionQuery query) {
        this.id = id;
        this.creationDate = checkNotNull(creationDate, "creationDate");
        this.query = checkNotNull(query, "query");
    }

    @Ignore
    public QueryHistory(ConnectionQuery query) {
        this.id = 0;
        this.creationDate = new Date();
        this.query = checkNotNull(query, "query");
    }


    public long getId() {
        return id;
    }

    @NonNull
    public ConnectionQuery getQuery() {
        return query;
    }

    @NonNull
    public Date getCreationDate() {
        return creationDate;
    }
}
