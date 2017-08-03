package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import ch.unstable.ost.utils.ParcelCompat;
import ch.unstable.ost.utils.ParcelUtils;

public class Location implements Parcelable, ch.unstable.ost.api.model.Location {
    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    @NonNull
    private final String id;
    private final String name;
    private final Coordinates coordinates;
    @Nullable
    private final InternalType internalType;

    public Location(String id, @Nullable InternalType internalType, String name, Coordinates coordinates) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        this.id = id;
        this.internalType = internalType;
        this.name = name;
        this.coordinates = coordinates;
    }

    protected Location(Parcel in) {
        id = in.readString();
        if (id == null) {
            throw new IllegalStateException("id is null");
        }
        name = in.readString();
        coordinates = ParcelCompat.readTypeObject(in, Coordinates.CREATOR);
        internalType = ParcelUtils.readEnum(InternalType.values(), in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        ParcelCompat.writeTypeObject(dest, coordinates, flags);
        ParcelUtils.writeEnum(dest, internalType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public ch.unstable.ost.api.model.Location.StationType getType() {
        if (internalType != null) {
            switch (internalType) {
                case STATION:
                    return StationType.TRAIN;
                case POI:
                    return StationType.POI;
                case ADDRESS:
                    return StationType.ADDRESS;
                case REFINE:
                    return StationType.UNKNOWN;
            }
        }
        return StationType.UNKNOWN;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(name, that.name) &&
                Objects.equal(coordinates, that.coordinates) &&
                internalType == that.internalType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, coordinates, internalType);
    }


    enum InternalType {
        STATION, POI, ADDRESS, REFINE
    }
}
