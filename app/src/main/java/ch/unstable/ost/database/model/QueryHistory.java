package ch.unstable.ost.database.model;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import ch.unstable.ost.api.model.ConnectionQuery;

@Entity(tableName = QueryHistory.TABLE_NAME)
public class QueryHistory {

    public static final String TABLE_NAME = "queries";

    @PrimaryKey(autoGenerate = true)
    private final long id;

    private final Date creationDate;

    @Embedded
    private final ConnectionQuery query;

    public QueryHistory(long id, Date creationDate, ConnectionQuery query) {
        this.id = id;
        this.creationDate = creationDate;
        this.query = query;
    }

    @Ignore
    public QueryHistory(ConnectionQuery query) {
        this.id = 0;
        this.creationDate = new Date();
        this.query = query;
    }


    public long getId() {
        return id;
    }

    public ConnectionQuery getQuery() {
        return query;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
