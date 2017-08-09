package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.StopTime;
import ch.unstable.ost.utils.ParcelUtils;

public class Section implements Parcelable, ch.unstable.ost.api.model.Section {

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
    private final Journey journey;

    @Nullable
    private final Walk walk;

    @NonNull
    private final Checkpoint departure;
    @NonNull
    private final Checkpoint arrival;


    public Section(Checkpoint departure, Checkpoint arrival,
                   @Nullable Journey journey, @Nullable Walk walk) {
        this.journey = journey;
        this.walk = walk;
        if (departure == null) throw new NullPointerException("departure is null");
        this.departure = departure;
        if (arrival == null) throw new NullPointerException("arrival is null");
        this.arrival = arrival;
    }

    protected Section(Parcel in) {
        journey = ParcelUtils.readNullableParcelable(in, Journey.CREATOR);
        walk = ParcelUtils.readNullableParcelable(in, Walk.CREATOR);
        departure = ParcelUtils.readNonNulTypedObject(in, Checkpoint.CREATOR);
        arrival = ParcelUtils.readNonNulTypedObject(in, Checkpoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeNullableParcelable(dest, journey, flags);
        ParcelUtils.writeNullableParcelable(dest, walk, flags);
        ParcelUtils.writeNonNullTypedObject(dest, departure, flags);
        ParcelUtils.writeNonNullTypedObject(dest, arrival, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Journey getJourney() {
        return journey;
    }

    public Checkpoint getDeparture() {
        return departure;
    }

    public Checkpoint getArrival() {
        return arrival;
    }

    public boolean isWalk() {
        return walk != null;
    }

    public boolean isJourney() {
        return journey != null;
    }

    @Override
    public StopTime[] getStops() {
        return getJourney().getPassList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (journey != null ? !journey.equals(section.journey) : section.journey != null)
            return false;
        if (!departure.equals(section.departure)) return false;
        return arrival.equals(section.arrival);

    }

    @Override
    public int hashCode() {
        int result = journey != null ? journey.hashCode() : 0;
        result = 31 * result + departure.hashCode();
        result = 31 * result + arrival.hashCode();
        return result;
    }

    @Nullable
    public Walk getWalk() {
        return walk;
    }

    @Nullable
    @Override
    public String getLineShortName() {
        if(journey != null) {
            if(journey.getNumber() == null || journey.getCategory() == null) {
                return journey.getName();
            } else {
                return journey.getCategory() + " " + journey.getNumber();
            }
        }
        return null;
    }

    @Override
    public Date getDepartureDate() {
        return getDeparture().getDepartureTime();
    }

    @Override
    public Date getArrivalDate() {
        return getArrival().getArrival();
    }

    @Override
    public Location getArrivalLocation() {
        return getArrival().getStation();
    }

    @Override
    public Location getDepartureLocation() {
        return getDeparture().getStation();
    }

    @Override
    public String getHeadsign() {
        return getJourney().getTo();
    }

    @Override
    public String getDeparturePlatform() {
        return getDeparture().getPlatform();
    }

    @Override
    public String getArrivalPlatform() {
        return getArrival().getPlatform();
    }
}
