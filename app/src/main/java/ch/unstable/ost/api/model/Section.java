package ch.unstable.ost.api.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

public interface Section extends Parcelable {
    /**
     * Get a short name for the used line
     * @return the short name of the line if it is not available
     */
    @Nullable
    String getLineShortName();

    Date getDepartureDate();

    Date getArrivalDate();

    Location getArrivalLocation();

    Location getDepartureLocation();

    String getHeadsign();

    String getDeparturePlatform();

    String getArrivalPlatform();

    boolean isJourney();

    StopTime[] getStops();
}
