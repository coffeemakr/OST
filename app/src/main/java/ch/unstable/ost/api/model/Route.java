package ch.unstable.ost.api.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

public class Route implements Parcelable {
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
    private final String shortName;
    @NonNull
    private final String longName;
    private final PassingCheckpoint[] stops;

    public Route(String shortname, String longName, PassingCheckpoint[] stops) {
        this.shortName = checkNotNull(shortname, "shortname");
        this.longName = checkNotNull(longName, "longName");
        this.stops = checkNotNull(stops, "stops");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equal(shortName, route.shortName) &&
                Objects.equal(longName, route.longName) &&
                Arrays.equals(stops, route.stops);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shortName, longName, Arrays.hashCode(stops));
    }
}
