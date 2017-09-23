package ch.unstable.ost.api.search.types;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.api.model.Route;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.utils.LogUtils;

import static ch.unstable.ost.api.transport.types.LocationDeserializer.getNullableString;

public class SectionsDeserializer implements JsonDeserializer<Section[]> {
    private static final String TAG = "SectionsDeserializer";
    private static final Logger LOGGER = Logger.getLogger(TAG);

    @NonNull
    private static Location getLocation(JsonObject jsonObject) {
        String name = jsonObject.get("sbb_name").getAsString();
        String id = jsonObject.get("stopid").getAsString();
        return new Location(name, Location.StationType.UNKNOWN, id);
    }

    private static ArrivalCheckpoint getArrival(SimpleDateFormat dateFormat, JsonObject object) {
        JsonObject exit = object.get("exit").getAsJsonObject();
        Date arrivalTime = PassingCheckpointsDeserializer.getDate(dateFormat, exit, "arrival");
        Location arrivalLocation = getLocation(exit);
        String arrivalPlatform = getNullableString(exit, "track");
        return new ArrivalCheckpoint(arrivalTime, arrivalPlatform, arrivalLocation);
    }

    public static void removeNulls(List list) {
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            if(iterator.next() == null) {
                iterator.remove();
            }
        }
    }

    @Override
    public Section[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SimpleDateFormat dateFormat = PassingCheckpointsDeserializer.getDateFormat();
        JsonArray jsonArray = json.getAsJsonArray();
        ArrayList<Section> sections = new ArrayList<>(jsonArray.size());
        for(JsonElement element: jsonArray) {
            JsonObject object = element.getAsJsonObject();
            String type;
            if(!object.has("type")) {
                LOGGER.severe("No type defined in " + LogUtils.prettyJson(object) + " \n Parent: " + LogUtils.prettyJson(json));
                // can be ignored probably
                continue;
            } else {
                type = object.get("type").getAsString();
            }

            if(type.equals("walk")) {
                if(BuildConfig.DEBUG) Log.d(TAG, "Walk not handled: " + LogUtils.prettyJson(object));
            } else {
                if(BuildConfig.DEBUG) Log.d(TAG, "Got type " + type + ": " + LogUtils.prettyJson(object));
                String shortname = object.get("line").getAsString();
                String longName = object.get("number").getAsString();
                Type listOfPassingCheckpoints = new TypeToken<List<PassingCheckpoint>>(){}.getType();
                List<PassingCheckpoint> stops = context.deserialize(object.get("stops"), listOfPassingCheckpoints);
                removeNulls(stops);
                Route route = new Route(shortname, longName, stops.toArray(new PassingCheckpoint[stops.size()]));

                Location departureLocation = getLocation(object);
                Date departureTime = PassingCheckpointsDeserializer.getDate(dateFormat, object, "departure");
                String platform = getNullableString(object, "track");
                DepartureCheckpoint departure = new DepartureCheckpoint(departureTime, platform, departureLocation);

                ArrivalCheckpoint arrival = getArrival(dateFormat, object);
                String headSign = object.get("terminal").getAsString();
                Section section = new Section(route, departure, arrival, headSign, 0);
                sections.add(section);
            }
        }
        return sections.toArray(new Section[sections.size()]);
    }
}
