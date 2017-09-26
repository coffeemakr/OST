package ch.unstable.ost.api.transport.types;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;

import static ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer.getPlatform;

public enum PassingCheckpointDeserializer implements JsonDeserializer<PassingCheckpoint> {
    INSTANCE;
    private static final String TAG = "PassCPDeserializer";

    @Nullable
    private static Date getNullableDate(JsonObject jsonObject, String field) {
        JsonElement value = jsonObject.get(field);
        if (value.isJsonNull()) {
            return null;
        } else {
            return new Date(value.getAsLong() * 1000);
        }
    }

    @Override
    public PassingCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject passObj = json.getAsJsonObject();
        Location location = LocationDeserializer.INSTANCE.deserialize(passObj.get("station"), null, context);

        Date arrival = getNullableDate(passObj, "arrivalTimestamp");
        if (arrival == null) {
            Log.w(TAG, "arrivalTimestamp is null: " + passObj.toString());
        }
        Date departure = getNullableDate(passObj, "departureTimestamp");
        if (departure == null) {
            Log.w(TAG, "departureTime is null: " + passObj.toString());
        }
        if (arrival == null && departure != null) {
            arrival = departure;
        } else if (arrival != null && departure == null) {
            departure = arrival;
        } else if (arrival == null) {
            Log.e(TAG, "departure and arrival is null");
            arrival = departure = new Date(0);
        }
        String platform = getPlatform(passObj);
        return new PassingCheckpoint(arrival, departure, location, platform);
    }
}
