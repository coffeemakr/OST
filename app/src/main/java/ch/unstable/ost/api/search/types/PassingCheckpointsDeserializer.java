package ch.unstable.ost.api.search.types;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.utils.LogUtils;

public enum PassingCheckpointsDeserializer implements JsonDeserializer<PassingCheckpoint> {
    INSTANCE;

    private static final String TAG = "PassingCPDeserializer";
    private static final Logger LOGGER = Logger.getLogger(TAG);

    @Nullable
    public static Date getDate(SimpleDateFormat dateFormat, JsonObject object, String name) {
        if (!object.has(name)) {
            return null;
        }
        JsonElement dateField = object.get(name);
        if (dateField.isJsonNull()) {
            return null;
        }
        try {
            return dateFormat.parse(dateField.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException("Unable to parse departure date", e);
        }
    }

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat departureFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);
        departureFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        return departureFormat;
    }

    @Override
    public PassingCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SimpleDateFormat dateFormat = getDateFormat();
        JsonObject object = json.getAsJsonObject();
        String stopId = object.get("stopid").getAsString();
        String name = object.get("name").getAsString();
        Date departure = getDate(dateFormat, object, "departure");
        Date arrival = getDate(dateFormat, object, "arrival");

        if (departure == null && arrival != null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Departure is null");
            departure = arrival;
        } else if (arrival == null && departure != null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Arrival is null");
            arrival = departure;
        } else if (arrival == null) {
            if (BuildConfig.DEBUG)
                Log.w(TAG, "Neither arrival nor departure is set: " + LogUtils.prettyJson(object));
            return null;
        }

        Location location = new Location(stopId, name, Location.StationType.UNKNOWN);
        // TODO: Find out if track is sent
        String platform = null;
        if (object.has("track")) {
            platform = object.get("track").getAsString();
        }
        return new PassingCheckpoint(arrival, departure, location, platform);
    }
}
