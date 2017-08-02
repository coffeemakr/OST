package ch.unstable.ost.api.transport.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ch.unstable.ost.utils.ParcelCompat;
import ch.unstable.ost.utils.ParcelUtils;

public class Checkpoint implements Parcelable {

    public static final Creator<Checkpoint> CREATOR = new Creator<Checkpoint>() {
        @Override
        public Checkpoint createFromParcel(Parcel in) {
            return new Checkpoint(in);
        }

        @Override
        public Checkpoint[] newArray(int size) {
            return new Checkpoint[size];
        }
    };
    private final OSLocation station;
    @Nullable
    @SerializedName("arrival")
    private final Date arrival;
    @Nullable
    @SerializedName("departure")
    private final Date departureTime;
    @Nullable
    private final Integer delay;
    /**
     * The platform the train ...?
     * <p>
     * Must be a string for stations with unidentified platforms.
     * e.g in Zürich: 43/44
     */
    @Nullable
    private final String platform;

    Checkpoint(OSLocation station) {
        this(station, null, null, null, null);
    }


    Checkpoint(OSLocation station, @Nullable Date arrival, @Nullable Date departureTime, @Nullable Integer delay, @Nullable String platform) {
        if (station == null) throw new NullPointerException("station is null");
        this.station = station;
        this.platform = platform;
        this.departureTime = departureTime;
        this.arrival = arrival;
        this.delay = delay;
    }

    protected Checkpoint(Parcel in) {
        this.station = ParcelCompat.readTypeObject(in, OSLocation.CREATOR);
        this.platform = in.readString();
        this.departureTime = ParcelUtils.readDate(in);
        this.arrival = ParcelUtils.readDate(in);
        this.delay = ParcelUtils.readNullableInteger(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelCompat.writeTypeObject(dest, station, flags);
        dest.writeString(platform);
        ParcelUtils.writeDate(dest, departureTime);
        ParcelUtils.writeDate(dest, arrival);
        ParcelUtils.writeNullableInteger(dest, delay);
    }

    @Override
    public String toString() {
        return station.getName() + "@" + arrival + " -> " + departureTime;
    }

    public OSLocation getStation() {
        return station;
    }

    public Date getArrival() {
        return arrival;
    }

    @Nullable
    public Date getDepartureTime() {
        return departureTime;
    }

    @Nullable
    public Integer getDelay() {
        return delay;
    }

    @Nullable
    public String getPlatform() {
        return platform;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Checkpoint that = (Checkpoint) o;

        if (!station.equals(that.station)) return false;
        if (arrival != null ? !arrival.equals(that.arrival) : that.arrival != null) return false;
        if (departureTime != null ? !departureTime.equals(that.departureTime) : that.departureTime != null)
            return false;
        if (delay != null ? !delay.equals(that.delay) : that.delay != null) return false;
        return platform != null ? platform.equals(that.platform) : that.platform == null;

    }

    @Override
    public int hashCode() {
        int result = station.hashCode();
        result = 31 * result + (arrival != null ? arrival.hashCode() : 0);
        result = 31 * result + (departureTime != null ? departureTime.hashCode() : 0);
        result = 31 * result + (delay != null ? delay.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        return result;
    }
}


