package ch.unstable.ost.api.transport;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import ch.unstable.ost.preference.StationDaoLoader;

@Keep
public class StationsDAOFactory implements StationDaoLoader.StationDAOFactory {
    @NonNull
    @Override
    public TransportAPI getStationsDAO(@NonNull Context context) {
        return new TransportAPI();
    }
}
