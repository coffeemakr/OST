package ch.unstable.ost.api.search.types;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.api.model.Location;

public class SearchCHIconClassDeserializer implements JsonDeserializer<Location.StationType> {
    @Override
    public Location.StationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String iconClass = json.getAsString();
        switch (iconClass) {
            case "sl-icon-type-zug":
                return Location.StationType.TRAIN;
            case "sl-icon-type-bus":
                return Location.StationType.BUS;
            case "sl-icon-type-adr":
                return Location.StationType.ADDRESS;
            default:
                return Location.StationType.UNKNOWN;
        }
    }
}
