package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class PassingCheckpoint extends Checkpoint implements Parcelable {

    public static final Creator<PassingCheckpoint> CREATOR = new Creator<PassingCheckpoint>() {
        @Override
        public PassingCheckpoint createFromParcel(Parcel in) {
            return new PassingCheckpoint(in);
        }

        @Override
        public PassingCheckpoint[] newArray(int size) {
            return new PassingCheckpoint[size];
        }
    };

    private final Date departureTime;
    private final Date arrivalTime;

    public PassingCheckpoint(Date arrivalTime, Date departureTime, Location location, String platform) {
        super(platform, location);
        this.arrivalTime = checkNotNull(arrivalTime, "arrivalTime");
        this.departureTime = checkNotNull(departureTime, "departureTime");
    }

    private PassingCheckpoint(Parcel in) {
        super(in);
        departureTime = ParcelUtils.readDate(in);
        arrivalTime = ParcelUtils.readDate(in);
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
        ParcelUtils.writeDate(dest, arrivalTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PassingCheckpoint that = (PassingCheckpoint) o;
        return Objects.equal(departureTime, that.departureTime) &&
                Objects.equal(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), departureTime, arrivalTime);
    }
}
