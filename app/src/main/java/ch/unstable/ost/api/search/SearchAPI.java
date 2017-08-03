package ch.unstable.ost.api.search;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ch.unstable.ost.api.TimetableDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.search.model.LocationCompletion;

public class SearchAPI extends BaseHttpJsonAPI implements TimetableDAO {

    final Uri BASE_URI = Uri.parse("https://timetable.search.ch/api/");

    @Override
    public Location[] getStationsByQuery(String query) throws IOException {
        return getStationsByQuery(query, null);
    }

    @Override
    @NonNull
    public Location[] getStationsByQuery(String query, @Nullable Location.StationType[] types) throws IOException {
        if (types != null && types.length == 0) return new Location[0];
        Uri.Builder builder = BASE_URI.buildUpon()
                .appendPath("completion.json")
                .appendQueryParameter("show_ids", "1")
                .appendQueryParameter("term", query);
        Type listType = new TypeToken<ArrayList<LocationCompletion>>() {
        }.getType();
        ArrayList<LocationCompletion> locationCompletions = loadJson(builder, listType);
        if (types != null) {
            locationCompletions = filterResults(locationCompletions, types);
        }
        return locationCompletions.toArray(new LocationCompletion[locationCompletions.size()]);
    }


    private ArrayList<LocationCompletion> filterResults(ArrayList<LocationCompletion> completions, final Location.StationType[] filter) {
        final int mask = Location.StationType.getMask(filter);
        ArrayList<LocationCompletion> filtered = new ArrayList<>(completions.size());
        for (LocationCompletion completion : completions) {
            if ((completion.getType().bit & mask) > 0) {
                filtered.add(completion);
            }
        }
        return filtered;
    }
}
