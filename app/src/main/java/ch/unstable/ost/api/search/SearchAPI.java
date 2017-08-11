package ch.unstable.ost.api.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.base.BaseHttpJsonAPI;
import ch.unstable.ost.api.search.types.LocationDeserializer;
import ch.unstable.ost.api.search.types.SearchCHIconClassDeserializer;
import io.mikael.urlbuilder.UrlBuilder;

public class SearchAPI extends BaseHttpJsonAPI implements StationsDAO {

    private final static String BASE_URI = "https://timetable.search.ch/api/";
    private final static String COMPLETION_URL = BASE_URI + "completion.json";

    @Override
    protected void onBuildGsonCreated(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(ch.unstable.ost.api.model.impl.Location.StationType.class, new SearchCHIconClassDeserializer());
        gsonBuilder.registerTypeAdapter(ch.unstable.ost.api.model.impl.Location.class, LocationDeserializer.INSTANCE);
    }

    @Override
    public ch.unstable.ost.api.model.impl.Location[] getStationsByQuery(String query) throws IOException {
        return getStationsByQuery(query, null);
    }

    @Override
    @NonNull
    public ch.unstable.ost.api.model.impl.Location[] getStationsByQuery(String query, @Nullable ch.unstable.ost.api.model.impl.Location.StationType[] types) throws IOException {
        if (types != null && types.length == 0) return new ch.unstable.ost.api.model.impl.Location[0];
        UrlBuilder builder = UrlBuilder.fromString(COMPLETION_URL)
                .addParameter("show_ids", "1")
                .addParameter("term", query);
        Type listType = new TypeToken<ArrayList<ch.unstable.ost.api.model.impl.Location>>() {
        }.getType();
        ArrayList<ch.unstable.ost.api.model.impl.Location> locationCompletions = loadJson(builder.toUrl(), listType);
        if (types != null) {
            locationCompletions = filterResults(locationCompletions, types);
        }
        return locationCompletions.toArray(new ch.unstable.ost.api.model.impl.Location[locationCompletions.size()]);
    }


    private ArrayList<ch.unstable.ost.api.model.impl.Location> filterResults(ArrayList<ch.unstable.ost.api.model.impl.Location> completions, final ch.unstable.ost.api.model.impl.Location.StationType[] filter) {
        final int mask = ch.unstable.ost.api.model.impl.Location.StationType.getMask(filter);
        ArrayList<ch.unstable.ost.api.model.impl.Location> filtered = new ArrayList<>(completions.size());
        for (ch.unstable.ost.api.model.impl.Location completion : completions) {
            if ((completion.getType().bit & mask) > 0) {
                filtered.add(completion);
            }
        }
        return filtered;
    }
}
