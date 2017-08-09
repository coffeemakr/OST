package ch.unstable.ost.api.model.impl;


import android.os.Parcel;
import android.os.Parcelable;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

public class Route implements Parcelable {
    private final String shortName;
    private final String headsign;

    public Route(String shortname, String headsign) {
        this.shortName = requireNonNull(shortname, "shortname");
        this.headsign = requireNonNull(headsign, "headsign");
    }

    protected Route(Parcel in) {
        shortName = in.readString();
        headsign = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shortName);
        dest.writeString(headsign);
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

    public String getShortName() {
        return shortName;
    }

    public String getHeadsign() {
        return headsign;
    }
}
