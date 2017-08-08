package ch.unstable.ost.api.offline.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "stations")
public class LocationEntity implements ch.unstable.ost.api.model.Location, Parcelable{

    private final int frequency;

    private final String name;
    @PrimaryKey
    private final String id;

    private final StationType[] types;

    public LocationEntity(String id, String name, StationType[] types, int frequency) {
        if(types.length == 0) throw new IllegalArgumentException("types must have at least length 1");
        this.name = name;
        this.id = id;
        this.types = types;
        this.frequency = frequency;
    }

    private LocationEntity(Parcel in) {
        name = in.readString();
        id = in.readString();
        types = StationType.fromMask(in.readInt());
        frequency = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeInt(StationType.getMask(types));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationEntity> CREATOR = new Creator<LocationEntity>() {
        @Override
        public LocationEntity createFromParcel(Parcel in) {
            return new LocationEntity(in);
        }

        @Override
        public LocationEntity[] newArray(int size) {
            return new LocationEntity[size];
        }
    };

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public StationType getType() {
        // TODO: Choose something wise?
        return types[0];
    }


    @NonNull
    public StationType[] getTypes() {
        return types;
    }

    public int getFrequency() {
        return frequency;
    }
}
