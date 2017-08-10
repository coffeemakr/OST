package ch.unstable.ost.api.model.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;


public class Location implements ch.unstable.ost.api.model.Location, Parcelable {
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
    private final String name;
    private final StationType type;
    @Nullable
    private final String id;

    public Location(String name, StationType type, @Nullable String id) {
        this.name = requireNonNull(name, "name");
        this.type = requireNonNull(type, "type");
        this.id = id;
    }

    protected Location(Parcel in) {
        name = in.readString();
        id = in.readString();
        type = ParcelUtils.readEnum(StationType.values(), in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        ParcelUtils.writeEnum(dest, type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getId() {
        if(id == null) {
            return name;
        } else {
            return id;
        }
    }

    @NonNull
    @Override
    public StationType getType() {
        return type;
    }
}
