package ch.unstable.ost.api.transport.types;

import android.support.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Locale;

import ch.unstable.ost.api.model.impl.Location;


public enum LocationDeserializer implements JsonDeserializer<Location> {
    INSTANCE;

    @Nullable
    public static String getNullableString(JsonObject parent, String field) {
        JsonElement value = parent.get(field);
        if(value.isJsonNull()) {
            return null;
        } else {
            return value.getAsString();
        }
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject location = json.getAsJsonObject();
        String id = getNullableString(location, "id");
        String name = location.get("name").getAsString();
        ch.unstable.ost.api.model.Location.StationType type = ch.unstable.ost.api.model.Location.StationType.UNKNOWN;
        if(location.has("type")) {
            String typeName = location.get("type").getAsString();
            switch (typeName.toLowerCase(Locale.ROOT)) {
                case "station":
                    type = ch.unstable.ost.api.model.Location.StationType.TRAIN;
                    break;
                case "poi":
                    type = ch.unstable.ost.api.model.Location.StationType.POI;
                    break;
                case "address":
                    type = ch.unstable.ost.api.model.Location.StationType.ADDRESS;
                    break;
                default:
                    throw new JsonParseException("Unknown type: " + typeName);
            }
        }
        return new Location(name, type, id);
    }
}
