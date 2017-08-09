package ch.unstable.ost.api.model;


import android.os.Parcelable;

import java.util.Date;

public interface Connection extends Parcelable {
    Section[] getSections();

    Date getDepartureDate();
    Date getArrivalDate();
}
