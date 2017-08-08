package ch.unstable.ost.preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import ch.unstable.ost.R;
import ch.unstable.ost.api.StationsDAO;

public class StationDaoLoader {
    public interface StationDAOFactory {
        @NonNull
        StationsDAO getStationsDAO(@NonNull Context context);
    }

    @NonNull
    public static StationsDAO createStationDAO(final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = sharedPreferences.getString(PreferenceKeys.KEY_STATIONS_DAO, context.getString(R.string.prefs_station_dao_factory_default));

        try {
            Class<?> clazz = Class.forName(preference);
            StationDAOFactory daoFactory = (StationDAOFactory) clazz.newInstance();
            return daoFactory.getStationsDAO(context);
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't get factory for StationsDAO", e);
        }
    }
}
