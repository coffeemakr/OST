package ch.unstable.ost.api.transport.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import ch.unstable.ost.api.model.impl.DepartureCheckpoint;
import ch.unstable.ost.api.model.impl.Location;

public enum DepartureCheckpointDeserializer implements JsonDeserializer<DepartureCheckpoint> {
    INSTANCE;
    private static final String TAG = "DepCPDeserializer";
    private static final String FIELD_PLATFORM = "platform";
    private static final String FIELD_DEPARTURE_TIMESTAMP = "departureTimestamp";
    private static final String FIELD_STATION = "station";

    @Nullable
    public static String getPlatform(JsonObject object) {
        if(object.has(FIELD_PLATFORM)) return null;
        JsonElement platform = object.get(FIELD_PLATFORM);
        if(platform.isJsonNull()) {
            return null;
        } else {
            return platform.getAsString().trim();
        }
    }

    @Override
    public DepartureCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "Getting departure from: " + json);
        JsonObject passObj = json.getAsJsonObject();
        Location location = LocationDeserializer.INSTANCE.deserialize(passObj.get(FIELD_STATION), null, context);

        Date departure = getDate(passObj, FIELD_DEPARTURE_TIMESTAMP);
        String platform = getPlatform(passObj);
        return new DepartureCheckpoint(departure, platform, location);
    }

    @NonNull
    public static Date getDate(JsonObject obj, String fieldName) {
        return new Date(obj.get(fieldName).getAsLong() * 1000);
    }
}
