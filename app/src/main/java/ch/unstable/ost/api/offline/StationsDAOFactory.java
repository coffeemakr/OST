package ch.unstable.ost.api.offline;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import ch.unstable.ost.database.Databases;
import ch.unstable.ost.preference.StationDaoLoader;

@Keep
public class StationsDAOFactory implements StationDaoLoader.StationDAOFactory {
    @NonNull
    @Override
    public OfflineStationsDAO getStationsDAO(@NonNull Context context) {
        StationsDatabase database = Databases.getStationsDatabase(context);
        return database.getStationsDAO();
    }
}
