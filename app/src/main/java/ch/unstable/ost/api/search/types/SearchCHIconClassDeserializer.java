package ch.unstable.ost.api.search.types;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ch.unstable.ost.api.model.Station;

public class SearchCHIconClassDeserializer implements JsonDeserializer<Station.StationType> {
    @Override
    public Station.StationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String iconClass = json.getAsString();
        switch (iconClass) {
            case "sl-icon-type-zug":
                return Station.StationType.TRAIN;
            case "sl-icon-type-bus":
                return Station.StationType.BUS;
            case "sl-icon-type-adr":
                return Station.StationType.ADDRESS;
            default:
                return Station.StationType.UNKNOWN;
        }
    }
}
