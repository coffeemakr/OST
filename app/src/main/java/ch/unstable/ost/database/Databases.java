package ch.unstable.ost.database;


import android.arch.persistence.room.Room;
import android.content.Context;

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

    public static synchronized StationsDatabase getStationsDatabase(Context context) {
        if(stationsDatabase == null) {
            String databaseName = "stations.db";
            copyDBFromAssets(context, databaseName);
            stationsDatabase = Room.databaseBuilder(context.getApplicationContext(), StationsDatabase.class, databaseName).build();
        }
        return stationsDatabase;
    }

    public static void copyDBFromAssets(Context context, String database) {
        try {
            File databaseFile = context.getApplicationContext().getDatabasePath(database);
            File parentDir = databaseFile.getParentFile();
            if(!parentDir.exists()) {
                if(!parentDir.mkdirs()) {
                    throw new IOException("Couldn't create directory: " + parentDir);
                }
            }
            if(FORCE_OVERRIDE || !databaseFile.exists()) {
                InputStream inputStream = context.getAssets().open(database);
                OutputStream outputStream = new FileOutputStream(databaseFile);
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
