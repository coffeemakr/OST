package ch.unstable.ost.api.transport.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationDeserializer implements JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        return fromString(value);
    }

    static Long fromString(String value) {
        if(value.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("^(?:([0-9]{2})d)?([0-9]{2}):([0-9]{2}):([0-9]{2})$");
        Matcher matcher = pattern.matcher(value);
        if(matcher.matches()) {
            String daysString = matcher.group(1);
            int days = 0;
            if(daysString != null) {
                days = Integer.parseInt(daysString);
            }
            int hours = Integer.parseInt(matcher.group(2));
            int minutes = Integer.parseInt(matcher.group(3));
            int seconds = Integer.parseInt(matcher.group(4));
            return ((((days * 24L) + hours) * 60) + minutes) * 60 + seconds;
        }
        return null;
    }
}
