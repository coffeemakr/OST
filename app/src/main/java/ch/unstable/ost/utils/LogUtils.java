package ch.unstable.ost.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public enum LogUtils {
    ;

    /**
     * Get a pretty json from an json element.
     * @param element the json element
     * @return the pretty json
     */
    public static String prettyJson(JsonElement element) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        return gson.toJson(element);
    }
}
