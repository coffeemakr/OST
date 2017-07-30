package ch.unstable.ost.api.transport.model;


import android.support.annotation.NonNull;

public enum LocationTypeFilter {
    ALL, POI, ADDRESS, STATION;

    @NonNull
    public String getIdentifier() {
        return name().toLowerCase();
    }
}