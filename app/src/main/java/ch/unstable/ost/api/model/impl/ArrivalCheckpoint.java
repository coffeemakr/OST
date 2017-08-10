package ch.unstable.ost.api.model.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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

    protected ArrivalCheckpoint(Parcel in) {
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
}
