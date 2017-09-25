package ch.unstable.ost.database.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.utils.ParcelUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Favorite connection
 */
@Entity(tableName = FavoriteConnection.TABLE_NAME)
public class FavoriteConnection implements Parcelable {

    public final static String TABLE_NAME = "favorite_connections";
    public static final Creator<FavoriteConnection> CREATOR = new Creator<FavoriteConnection>() {
        @Override
        public FavoriteConnection createFromParcel(Parcel in) {
            return new FavoriteConnection(in);
        }

        @Override
        public FavoriteConnection[] newArray(int size) {
            return new FavoriteConnection[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private final Connection connection;

    @NonNull
    @ColumnInfo(name = "creation_date", index = true)
    private final Date creationDate;


    public FavoriteConnection(long id, @NonNull Connection connection, @NonNull Date creationDate) {
        this.id = id;
        this.connection = connection;
        this.creationDate = creationDate;
    }

    @Ignore
    public FavoriteConnection(@NonNull Connection connection) {
        this.id = 0;
        this.connection = connection;
        this.creationDate = new Date();
    }

    @Ignore
    protected FavoriteConnection(Parcel in) {
        id = in.readLong();
        connection = in.readParcelable(Connection.class.getClassLoader());
        creationDate = checkNotNull(ParcelUtils.readDate(in), "readDate returned null as creationDate");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(connection, flags);
        ParcelUtils.writeDate(dest, creationDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public Connection getConnection() {
        return connection;
    }

    @NonNull
    public Date getCreationDate() {
        return creationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
