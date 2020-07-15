package ch.unstable.ost.api.base;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.utils.StandartCharsetCompat;


public class BaseHttpJsonAPI {
    private static final int HTTP_CODE_TOO_MANY_REQUESTS = 429;
    private static final String TAG = "BaseHttpJsonAPI";
    private static final String USER_AGENT = "OST/" + BuildConfig.VERSION_NAME;
    private final Gson gson;

    protected BaseHttpJsonAPI() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        onBuildGsonCreated(gsonBuilder);
        gson = gsonBuilder.create();
    }


    protected void onBuildGsonCreated(GsonBuilder gsonBuilder) {
    }


    private InputStreamReader open(URL url) throws IOException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loading JSON " + url);
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        int code = urlConnection.getResponseCode();
        if (code == HTTP_CODE_TOO_MANY_REQUESTS) {
            throw new TooManyRequestsException();
        }
        return new InputStreamReader(urlConnection.getInputStream(), StandartCharsetCompat.UTF_8);
    }

    protected <T> T loadJson(URL url, Type typeOfT) throws IOException {
        InputStreamReader inputStreamReader = open(url);
        try {
            return gson.fromJson(inputStreamReader, typeOfT);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    protected <T> T loadJson(URL url, Class<T> jsonClass) throws IOException {
        InputStreamReader inputStreamReader = open(url);
        try {
            return gson.fromJson(inputStreamReader, jsonClass);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    public static class TooManyRequestsException extends IOException {
        public TooManyRequestsException() {
            super();
        }
    }
}
