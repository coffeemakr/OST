package ch.unstable.ost.api.transport.types;


import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.model.impl.ArrivalCheckpoint;
import ch.unstable.ost.api.model.impl.Connection;
import ch.unstable.ost.api.model.impl.DepartureCheckpoint;
import ch.unstable.ost.api.model.impl.Section;
import ch.unstable.ost.utils.LogUtils;

import static android.content.ContentValues.TAG;

public enum ConnectionDeserializer implements JsonDeserializer<Connection> {
    INSTANCE;

    @Override
    public Connection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject connectionObj = json.getAsJsonObject();
        Section[] sections = context.deserialize(connectionObj.get("sections"), Section[].class);
        if(sections == null) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "sections is null in \n" + LogUtils.prettyJson(json));
            }
            throw new JsonParseException("sections is null");
        }
        DepartureCheckpoint departure = context.deserialize(connectionObj.get("from"), DepartureCheckpoint.class);
        if(departure == null) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "from is null in \n" + LogUtils.prettyJson(json));
            }
            throw new JsonParseException("from is null");
        }
        ArrivalCheckpoint arrival = context.deserialize(connectionObj.get("to"), ArrivalCheckpoint.class);
        if(arrival == null) {
            if(BuildConfig.DEBUG) Log.e(TAG, "to is null in \n" + LogUtils.prettyJson(json));
            throw new JsonParseException("to is null");
        }
        return new Connection(sections, departure, arrival);
    }
}
