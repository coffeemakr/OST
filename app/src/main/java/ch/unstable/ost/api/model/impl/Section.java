package ch.unstable.ost.api.model.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;
import static ch.unstable.ost.utils.ParcelUtils.readParcelable;
import static ch.unstable.ost.utils.ParcelUtils.writeParcelable;


public class Section implements Parcelable{

    @NonNull
    private final DepartureCheckpoint departure;
    @NonNull
    private final ArrivalCheckpoint arrival;
    @NonNull
    private final String headsign;
    private final long walkTime;
    @NonNull
    private final Route route;

    public Section(Route route, DepartureCheckpoint departure, ArrivalCheckpoint arrival, String headsign, long walkTime) {
        this.route = requireNonNull(route, "route");
        this.departure = requireNonNull(departure, "departure");
        this.arrival = requireNonNull(arrival, "arrival");
        this.headsign = requireNonNull(headsign, "headsign");
        this.walkTime = walkTime;
    }

    protected Section(Parcel in) {
        route = readParcelable(in, Route.CREATOR);
        departure = readParcelable(in, DepartureCheckpoint.CREATOR);
        arrival = readParcelable(in, ArrivalCheckpoint.CREATOR);
        headsign = in.readString();
        walkTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeParcelable(dest, route, flags);
        writeParcelable(dest, departure, flags);
        writeParcelable(dest, arrival, flags);
        dest.writeString(headsign);
        dest.writeLong(walkTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    @Nullable
    public String getLineShortName() {
        return route.getShortName();
    }

    public Date getDepartureDate() {
        return departure.getDepartureTime();
    }

    public Date getArrivalDate() {
        return arrival.getArrivalTime();
    }

    public Location getArrivalLocation() {
        return arrival.getLocation();
    }

    public Location getDepartureLocation() {
        return departure.getLocation();
    }

    @NonNull
    public String getHeadsign() {
        return headsign;
    }

    public String getDeparturePlatform() {
        return departure.getPlatform();
    }

    public String getArrivalPlatform() {
        return arrival.getPlatform();
    }

    public PassingCheckpoint[] getStops() {
        return route.getStops();
    }

    @NonNull
    public String getRouteLongName() {
        return route.getLongName();
    }
}
