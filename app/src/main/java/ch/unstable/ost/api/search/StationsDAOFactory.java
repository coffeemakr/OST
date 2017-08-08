package ch.unstable.ost.api.search;


import android.content.Context;
import android.support.annotation.NonNull;

import ch.unstable.ost.preference.StationDaoLoader;

public class StationsDAOFactory implements StationDaoLoader.StationDAOFactory {
    @NonNull
    @Override
    public SearchAPI getStationsDAO(@NonNull Context context) {
        return new SearchAPI();
    }
}
