package ch.unstable.ost.api.transport.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ch.unstable.ost.api.model.StopTime;
import ch.unstable.ost.utils.ParcelCompat;
import ch.unstable.ost.utils.ParcelUtils;

public class Checkpoint implements StopTime, Parcelable {

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
    private final Location station;
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

    Checkpoint(Location station) {
        this(station, null, null, null, null);
    }


    Checkpoint(Location station, @Nullable Date arrival, @Nullable Date departureTime, @Nullable Integer delay, @Nullable String platform) {
        if (station == null) throw new NullPointerException("station is null");
        this.station = station;
        this.platform = platform;
        this.departureTime = departureTime;
        this.arrival = arrival;
        this.delay = delay;
    }

    protected Checkpoint(Parcel in) {
        this.station = ParcelCompat.readTypeObject(in, Location.CREATOR);
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

    public Location getStation() {
        return station;
    }

    @Nullable
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

    @Override
    public Location getLocation() {
        return station;
    }

    @Override
    public Date getTime() {
        if(departureTime != null) {
            return departureTime;
        } else {
            return arrival;
        }
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
        return Objects.equal(station, that.station) &&
                Objects.equal(arrival, that.arrival) &&
                Objects.equal(departureTime, that.departureTime) &&
                Objects.equal(delay, that.delay) &&
                Objects.equal(platform, that.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(station, arrival, departureTime, delay, platform);
    }
}


