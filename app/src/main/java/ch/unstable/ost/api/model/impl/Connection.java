package ch.unstable.ost.api.model.impl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

public class Connection implements Parcelable {

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel in) {
            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };
    private final Section[] sections;
    private DepartureCheckpoint departure;
    private ArrivalCheckpoint arrival;

    public Connection(Section[] sections, DepartureCheckpoint departure, ArrivalCheckpoint arrival) {
        this.sections = requireNonNull(sections, "sections");
        this.departure = requireNonNull(departure, "departure");
        this.arrival = requireNonNull(arrival, "arrival");
    }

    protected Connection(Parcel in) {
        sections = in.createTypedArray(Section.CREATOR);
        departure = ParcelUtils.readParcelable(in, DepartureCheckpoint.CREATOR);
        arrival = ParcelUtils.readParcelable(in, ArrivalCheckpoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(sections, flags);
        ParcelUtils.writeParcelable(dest, departure, flags);
        ParcelUtils.writeParcelable(dest, arrival, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Section[] getSections() {
        return sections;
    }

    public Date getDepartureDate() {
        return departure.getDepartureTime();
    }

    public Date getArrivalDate() {
        return arrival.getArrivalTime();
    }
}
