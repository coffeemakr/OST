package ch.unstable.ost.api.model.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

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
        this.arrivalTime = requireNonNull(arrivalTime, "arrivalTime");
        this.departureTime = requireNonNull(departureTime, "departureTime");
    }

    protected PassingCheckpoint(Parcel in) {
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
}
