package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

public class DepartureCheckpoint extends Checkpoint implements Parcelable {
    public static final Creator<DepartureCheckpoint> CREATOR = new Creator<DepartureCheckpoint>() {
        @Override
        public DepartureCheckpoint createFromParcel(Parcel in) {
            return new DepartureCheckpoint(in);
        }

        @Override
        public DepartureCheckpoint[] newArray(int size) {
            return new DepartureCheckpoint[size];
        }
    };
    private final Date departureTime;

    public DepartureCheckpoint(Date departureTime, String platform, Location location) {
        super(platform, location);
        this.departureTime = departureTime;
    }

    protected DepartureCheckpoint(Parcel in) {
        super(in);
        departureTime = ParcelUtils.readDate(in);
    }

    @NonNull
    @Override
    public Date getDisplayDate() {
        return departureTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelUtils.writeDate(dest, departureTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DepartureCheckpoint that = (DepartureCheckpoint) o;
        return Objects.equal(departureTime, that.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), departureTime);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("departureTime", departureTime)
                .add("location", getLocation())
                .add("platform", getPlatform())
                .toString();
    }
}
