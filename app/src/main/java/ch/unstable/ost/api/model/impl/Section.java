package ch.unstable.ost.api.model.impl;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.StopTime;
import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

public class Section implements ch.unstable.ost.api.model.Section, Parcelable {
    private final StopTime arrival;
    private final StopTime departure;
    private final Route route;

    public Section(StopTime arrival, StopTime departure, Route route) {
        this.arrival = requireNonNull(arrival, "arrival");
        this.departure = requireNonNull(departure, "departure");
        this.route = requireNonNull(route, "route");
    }

    protected Section(Parcel in) {
        arrival = in.readParcelable(StopTime.class.getClassLoader());
        departure = in.readParcelable(StopTime.class.getClassLoader());
        route = ParcelUtils.readNullableParcelable(in, Route.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(arrival, flags);
        dest.writeParcelable(departure, flags);
        ParcelUtils.writeNullableParcelable(dest, route, flags);
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
    @Override
    public String getLineShortName() {
        return route.getShortName();
    }

    @Override
    public Date getDepartureDate() {
        return departure.getTime();
    }

    @Override
    public Date getArrivalDate() {
        return arrival.getTime();
    }

    @Override
    public Location getArrivalLocation() {
        return arrival.getLocation();
    }

    @Override
    public Location getDepartureLocation() {
        return departure.getLocation();
    }

    @Override
    public String getHeadsign() {
        return route.getHeadsign();
    }

    @Override
    public String getDeparturePlatform() {
        return departure.getPlatform();
    }

    @Override
    public String getArrivalPlatform() {
        return arrival.getPlatform();
    }

    @Override
    public boolean isJourney() {
        return true;
    }

    @Override
    public StopTime[] getStops() {
        return new StopTime[0];
    }
}
