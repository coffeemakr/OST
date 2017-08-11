package ch.unstable.ost.api.transport.types;


import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.utils.LogUtils;

import static android.content.ContentValues.TAG;

public enum ConnectionDeserializer implements JsonDeserializer<Connection> {
    INSTANCE;

    @Override
    public Connection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject connectionObj = json.getAsJsonObject();
        Section[] sections = context.deserialize(connectionObj.get("sections"), Section[].class);
        if (sections == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "sections is null in \n" + LogUtils.prettyJson(json));
            }
            throw new JsonParseException("sections is null");
        }
        return new Connection(sections);
    }
}
