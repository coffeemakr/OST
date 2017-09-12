package ch.unstable.ost.api.search.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;

public class ConnectionDeserializer implements JsonDeserializer<Connection> {
    @Override
    public Connection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject connectionObj = json.getAsJsonObject();
        Section[] sections = context.deserialize(connectionObj.get("legs"), Section[].class);
        return new Connection(sections);
    }
}
