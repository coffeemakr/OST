package ch.unstable.ost.api.search.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.api.model.Location;


public enum LocationDeserializer implements JsonDeserializer<Location> {
    INSTANCE;

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject locationObj = json.getAsJsonObject();
        System.out.println(locationObj);
        String name = locationObj.get("label").getAsString();
        String id = locationObj.get("id").getAsString();
        Location.StationType stationType = context.deserialize(locationObj.get("iconclass"), Location.StationType.class);
        return new Location(name, stationType, id);
    }
}
