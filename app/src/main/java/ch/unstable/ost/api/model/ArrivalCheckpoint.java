package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

public class ArrivalCheckpoint extends Checkpoint implements Parcelable {
    public static final Creator<ArrivalCheckpoint> CREATOR = new Creator<ArrivalCheckpoint>() {
        @Override
        public ArrivalCheckpoint createFromParcel(Parcel in) {
            return new ArrivalCheckpoint(in);
        }

        @Override
        public ArrivalCheckpoint[] newArray(int size) {
            return new ArrivalCheckpoint[size];
        }
    };
    private final Date arrivalTime;

    public ArrivalCheckpoint(Date arrivalTime, String platform, Location location) {
        super(platform, location);
        this.arrivalTime = arrivalTime;
    }

    private ArrivalCheckpoint(Parcel in) {
        super(in);
        arrivalTime = ParcelUtils.readDate(in);
    }

    @NonNull
    @Override
    public Date getDisplayDate() {
        return arrivalTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ParcelUtils.writeDate(dest, arrivalTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArrivalCheckpoint that = (ArrivalCheckpoint) o;
        return Objects.equal(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), arrivalTime);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("arrivalTime", arrivalTime)
                .add("location", getLocation())
                .add("platform", getPlatform())
                .toString();
    }
}
