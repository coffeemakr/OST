package ch.unstable.ost.database;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.common.base.Preconditions;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.unstable.ost.api.offline.StationsDatabase;

public class Databases {
    private static final boolean FORCE_OVERRIDE = true;
    private static StationsDatabase stationsDatabase;
    private static CacheDatabase cacheDatabase;

    public static synchronized StationsDatabase getStationsDatabase(Context context) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(context, "context is null");
        if (stationsDatabase == null) {
            String databaseName = "stations.db";
            copyDBFromAssets(context, databaseName);
            stationsDatabase = Room.databaseBuilder(context.getApplicationContext(), StationsDatabase.class, databaseName).build();
        }
        return stationsDatabase;
    }

    public static synchronized CacheDatabase getCacheDatabase(Context context) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(context, "context is null");
        if (cacheDatabase == null) {
            String databaseName = "cache.db";
            cacheDatabase = Room.databaseBuilder(context.getApplicationContext(), CacheDatabase.class, databaseName).build();
        }
        return cacheDatabase;
    }

    public static void copyDBFromAssets(Context context, String database) {
        try {
            File databaseFile = context.getApplicationContext().getDatabasePath(database);
            File parentDir = databaseFile.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Couldn't create directory: " + parentDir);
                }
            }
            if (FORCE_OVERRIDE || !databaseFile.exists()) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = context.getAssets().open(database);
                    outputStream = new FileOutputStream(databaseFile);
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
