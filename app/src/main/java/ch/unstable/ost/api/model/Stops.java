package ch.unstable.ost.api.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;


public interface Stops extends Parcelable{
    String getStationName();

    @Nullable
    Date getDepartureTime();
}
