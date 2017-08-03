package ch.unstable.ost.api.model;

import android.os.Parcelable;

import java.util.Date;

public interface Section extends Parcelable {

    String getMoTFullName();

    String getMoTShortName();

    boolean isJourney();

    Date getDepartureTime();

    Date getArrivalTime();

    boolean isWalk();

    String getEndDestination();

    String getArrivalStationName();

    String getDepartureStationName();

    String getDeparturePlatform();

    String getArrivalPlatform();

    Stops[] getStops();
}
