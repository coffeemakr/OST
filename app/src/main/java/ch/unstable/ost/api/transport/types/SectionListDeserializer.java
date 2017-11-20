package ch.unstable.ost.api.transport.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.api.model.Route;
import ch.unstable.ost.api.model.Section;

import static ch.unstable.ost.api.transport.types.LocationDeserializer.getNullableString;

public enum SectionListDeserializer implements ch.unstable.ost.api.base.SectionListDeserializer {
    INSTANCE;

    public static final String FIELD_JOURNEY = "journey";
    public static final String FIELD_WALK = "walk";

    private static Section deserializeSection(JsonElement json, JsonDeserializationContext context, long walkTime) throws JsonParseException {
        JsonObject sectionObj = json.getAsJsonObject();
        JsonObject journey = sectionObj.get(FIELD_JOURNEY).getAsJsonObject();
        String headsign = getHeadsign(journey);
        String routeShortName = getRouteShortname(journey);
        String routeLongName = getRouteLongName(journey);
        if (routeShortName == null) {
            routeShortName = routeLongName;
        }
        DepartureCheckpoint departure = context.deserialize(sectionObj.get("departure"), DepartureCheckpoint.class);
        ArrivalCheckpoint arrival = context.deserialize(sectionObj.get("arrival"), ArrivalCheckpoint.class);
        PassingCheckpoint[] passingCheckpoints = context.deserialize(journey.get("passList"), PassingCheckpoint[].class);
        Route route = new Route(routeShortName, routeLongName, Arrays.asList(passingCheckpoints));
        return new Section(route, departure, arrival, headsign, walkTime);
    }

    @NonNull
    private static String getRouteLongName(JsonObject journey) {
        return journey.get("name").getAsString();
    }

    @Nullable
    private static String getRouteShortname(JsonObject journey) {
        String number = getNullableString(journey, "number");
        if (number != null) {
            String category = getNullableString(journey, "category");
            if (category != null && !number.startsWith(category)) {
                return category + " " + number;
            }
        }
        return null;
    }

    private static String getHeadsign(JsonObject journey) {
        return journey.get("to").getAsString();
    }

    private static long getWalkDuration(JsonObject walkObj) {
        Long duration = DurationDeserializer.fromString(getNullableString(walkObj, "duration"));
        if (duration == null) return 0;
        return duration;
    }

    @Override
    public List<Section> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonArray array = json.getAsJsonArray();
        final ArrayList<Section> sections = new ArrayList<>(array.size());
        long walkTime = 0;
        for (JsonElement element : array) {
            JsonObject sectionObj = element.getAsJsonObject();
            if (sectionObj.has(FIELD_JOURNEY) && !sectionObj.get(FIELD_JOURNEY).isJsonNull()) {
                Section section = deserializeSection(sectionObj, context, walkTime);
                walkTime = 0;
                sections.add(section);
            } else if (sectionObj.has(FIELD_WALK) && !sectionObj.get(FIELD_WALK).isJsonNull()) {
                walkTime = getWalkDuration(sectionObj.get(FIELD_WALK).getAsJsonObject());
            } else {
                throw new JsonParseException("Section is not a journey and not a walk");
            }
        }
        return sections;
    }
}
