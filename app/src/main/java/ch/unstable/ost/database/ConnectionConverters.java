package ch.unstable.ost.database;


import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    public static long dateToLong(@Nullable Date date) {
        if(date == null) return 0;
        return date.getTime() / 1000L;
    }

    @TypeConverter
    @Nullable
    public static Date longToDate(long date) {
        if(date == 0) return null;
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
