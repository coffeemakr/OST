package ch.unstable.ost.utils;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class LogUtils {
    private LogUtils() {
    }

    public static String prettyJson(JsonElement element) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        return gson.toJson(element);
    }
}
