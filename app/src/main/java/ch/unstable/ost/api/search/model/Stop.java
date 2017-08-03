package ch.unstable.ost.api.search.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ch.unstable.ost.api.model.Stops;
import ch.unstable.ost.utils.ParcelUtils;

class Stop implements Stops, Parcelable{

    private final String stopid;

    // "27.10.2016 20:02"
    private final Date departure;

    @SerializedName("name")
    private final String name;

    Stop(String stopid, Date departure, String name) {
        this.stopid = stopid;
        this.departure = departure;
        this.name = name;
    }

    protected Stop(Parcel in) {
        stopid = in.readString();
        name = in.readString();
        departure = ParcelUtils.readDate(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopid);
        dest.writeString(name);
        ParcelUtils.writeDate(dest, departure);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    @Override
    public String getStationName() {
        return name;
    }

    @Nullable
    @Override
    public Date getDepartureTime() {
        return departure;
    }
}
