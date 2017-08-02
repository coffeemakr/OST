package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

        OSLocation location = (OSLocation) o;

        if (!id.equals(location.id)) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (coordinates != null ? !coordinates.equals(location.coordinates) : location.coordinates != null)
            return false;
        return type == location.type;

    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public enum Type {
        STATION, POI, ADDRESS, REFINE;
    }
}
