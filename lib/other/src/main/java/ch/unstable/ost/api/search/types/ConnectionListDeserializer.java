package ch.unstable.ost.api.search.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.api.model.Connection;

/**
 * Created on 24.02.18.
 */

public class ConnectionListDeserializer implements JsonDeserializer<ConnectionsList> {
    @Override
    public ConnectionsList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        JsonArray array = object.get("connections").getAsJsonArray();
        Connection[] connections = context.deserialize(array, Connection[].class);
        if(connections == null) throw new JsonParseException("connections is null");
        return new ConnectionsList(connections);
    }
}
