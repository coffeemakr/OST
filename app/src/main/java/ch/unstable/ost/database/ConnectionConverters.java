package ch.unstable.ost.database;


import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Date;

import ch.unstable.ost.api.model.Connection;

public class ConnectionConverters {
    @TypeConverter
    public static String connectionArrayToString(Connection[] connections) {
        Gson gson = new Gson();
        return gson.toJson(connections);
    }

    @TypeConverter
    public static Connection[] connectionStringToArray(String connectionString) {
        Gson gson = new Gson();
        return gson.fromJson(connectionString, Connection[].class);
    }

    @TypeConverter
    public static long dateToLong(Date date) {
        return date.getTime() / 1000L;
    }

    @TypeConverter
    @NonNull
    public static Date longToDate(long date) {
        return new Date(date*1000L);
    }

    @TypeConverter
    @NonNull
    public static String stringArrayToCSV(String[] values) {
        return new Gson().toJson(values);
    }

    @TypeConverter
    @NonNull
    public static String[] csvToStringArray(String value) {
        return new Gson().fromJson(value, String[].class);
    }
}
