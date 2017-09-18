package ch.unstable.ost.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import ch.unstable.ost.api.model.Connection;

@Entity(tableName = CachedConnection.TABLE_NAME,
        foreignKeys = @ForeignKey(
                entity = QueryHistory.class,
                parentColumns = "id",
                childColumns = "query_id"),
        indices = {@Index(value = {"query_id", "sequence"},
                unique = true)})
public class CachedConnection {
    public final static String TABLE_NAME = "connection";

    private final Connection connection;

    @PrimaryKey(autoGenerate = true)
    private final long id;

    @ColumnInfo(name = "query_id")
    private final long queryId;
    private final int sequence;


    public CachedConnection(long id, long queryId, int sequence, Connection connection) {
        this.id = id;
        this.queryId = queryId;
        this.sequence = sequence;
        this.connection = connection;
    }

    @Ignore
    public CachedConnection(long queryId, int sequence, Connection connection) {
        this(0, queryId, sequence, connection);
    }

    public long getQueryId() {
        return queryId;
    }

    public int getSequence() {
        return sequence;
    }

    public Connection getConnection() {
        return connection;
    }

    public long getId() {
        return id;
    }
}
