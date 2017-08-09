package ch.unstable.ost.api.model;

import android.os.Parcelable;

import java.util.Date;

public interface StopTime extends Parcelable {
    Location getLocation();

    Date getTime();

    String getPlatform();
}
