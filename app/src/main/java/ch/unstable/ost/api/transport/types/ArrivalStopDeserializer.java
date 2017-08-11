package ch.unstable.ost.api.transport.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.Location;

import static ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer.getDate;
import static ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer.getPlatform;

public enum ArrivalStopDeserializer implements JsonDeserializer<ArrivalCheckpoint> {
    INSTANCE;
    private static final String TAG = "PassingCheckpointDeserializer";

    @Override
    public ArrivalCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject passObj = json.getAsJsonObject();
        Location location = LocationDeserializer.INSTANCE.deserialize(passObj.get("station"), null, context);

        Date arrival = getDate(passObj, "arrivalTimestamp");
        String platform = getPlatform(passObj);
        return new ArrivalCheckpoint(arrival, platform, location);
    }
}
