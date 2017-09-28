package ch.unstable.ost.api.base;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by coffeemakr on 28.09.17.
 */

public enum ThrowingAdapter implements JsonDeserializer<Object> {
    INSTANCE;
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        throw new JsonParseException("Type not allowed to be deserialzed: " + typeOfT)
    }
}
