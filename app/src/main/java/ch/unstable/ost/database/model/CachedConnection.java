package ch.unstable.ost.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;

@Entity(tableName = CachedConnection.TABLE_NAME)
public class CachedConnection {
    public final static String TABLE_NAME = "connections";

    @PrimaryKey(autoGenerate = true)
    public final long id;

    public final Date creationDate;

    @Embedded
    private final ConnectionQuery query;

    @ColumnInfo(name = "connection")
    public final Connection[] connections;


    public CachedConnection(long id, Date creationDate, ConnectionQuery query, Connection[] connections) {
        this.id = id;
        this.creationDate = creationDate;
        this.query = query;
        this.connections = connections;
    }

    public long getId() {
        return id;
    }

    public List<Connection> getConnections() {
        return Arrays.asList(connections);
    }

    public ConnectionQuery getQuery() {
        return query;
    }
}
