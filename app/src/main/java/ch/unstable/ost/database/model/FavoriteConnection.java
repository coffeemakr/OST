package ch.unstable.ost.database.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;

/**
 * Favorite connection
 */
@Entity(tableName = FavoriteConnection.TABLE_NAME)
public class FavoriteConnection {

    public final static String TABLE_NAME = "favorite_connections";
    @PrimaryKey
    private final long id;

    @NonNull
    private final Connection connection;
    @NonNull
    @Embedded
    private final ConnectionQuery query;

    @NonNull
    @ColumnInfo(name = "creation_date")
    private final Date creationDate;

    public FavoriteConnection(long id, @NonNull Connection connection, @NonNull ConnectionQuery query, @NonNull Date creationDate) {
        this.id = id;
        this.connection = connection;
        this.query = query;
        this.creationDate = creationDate;
    }

    @Ignore
    public FavoriteConnection(@NonNull Connection connection, @NonNull ConnectionQuery query) {
        this.id = 0;
        this.connection = connection;
        this.query = query;
        this.creationDate = new Date();
    }

    @NonNull
    public Connection getConnection() {
        return connection;
    }

    @NonNull
    public ConnectionQuery getQuery() {
        return query;
    }

    @NonNull
    public Date getCreationDate() {
        return creationDate;
    }

    public long getId() {
        return id;
    }
}
