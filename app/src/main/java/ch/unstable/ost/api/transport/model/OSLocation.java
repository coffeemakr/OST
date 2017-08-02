package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import ch.unstable.ost.utils.ParcelCompat;
import ch.unstable.ost.utils.ParcelUtils;

public class OSLocation implements Parcelable {
    public static final Creator<OSLocation> CREATOR = new Creator<OSLocation>() {
        @Override
        public OSLocation createFromParcel(Parcel in) {
            return new OSLocation(in);
        }

        @Override
        public OSLocation[] newArray(int size) {
            return new OSLocation[size];
        }
    };
    private final String id;
    private final String name;
    private final Coordinates coordinates;
    @Nullable
    private final Type type;

    public OSLocation(@NonNull String id, @Nullable Type type, String name, Coordinates coordinates) {
        if (id == null) throw new NullPointerException("id is null");
        this.id = id;
        this.type = type;
        this.name = name;
        this.coordinates = coordinates;
    }

    protected OSLocation(Parcel in) {
        id = in.readString();
        if (id == null) {
            throw new IllegalStateException("id is null");
        }
        name = in.readString();
        coordinates = ParcelCompat.readTypeObject(in, Coordinates.CREATOR);
        type = ParcelUtils.readEnum(Type.values(), in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        ParcelCompat.writeTypeObject(dest, coordinates, flags);
        ParcelUtils.writeEnum(dest, type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OSLocation that = (OSLocation) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(name, that.name) &&
                Objects.equal(coordinates, that.coordinates) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, coordinates, type);
    }


    public enum Type {
        STATION, POI, ADDRESS, REFINE;
    }
}
