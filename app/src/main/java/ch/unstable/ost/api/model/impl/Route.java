package ch.unstable.ost.api.model.impl;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

public class Route implements Parcelable {
    @NonNull
    private final String shortName;
    @NonNull
    private final String longName;
    private final PassingCheckpoint[] stops;

    public Route(String shortname, String longName, PassingCheckpoint[] stops) {
        this.shortName = requireNonNull(shortname, "shortname");
        this.longName = requireNonNull(longName, "longName");
        this.stops = requireNonNull(stops, "stops");
    }

    protected Route(Parcel in) {
        shortName = in.readString();
        longName = in.readString();
        stops = in.createTypedArray(PassingCheckpoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shortName);
        dest.writeString(longName);
        dest.writeTypedArray(stops, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @NonNull
    public String getShortName() {
        return shortName;
    }

    @NonNull
    public String getLongName() {
        return longName;
    }

    public PassingCheckpoint[] getStops() {
        return stops;
    }
}
