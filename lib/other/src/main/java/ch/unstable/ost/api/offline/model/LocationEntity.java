package ch.unstable.ost.api.offline.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import ch.unstable.ost.api.model.Location;

@Entity(tableName = "stations")
public class LocationEntity {

    private final int frequency;
    private final String name;
    @PrimaryKey
    @NonNull
    private final String id;
    private final Location.StationType[] types;

    public LocationEntity(@NotNull String id, String name, Location.StationType[] types, int frequency) {
        if (types.length == 0)
            throw new IllegalArgumentException("types must have at least length 1");
        this.name = name;
        this.id = id;
        this.types = types;
        this.frequency = frequency;
    }


    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }


    @NonNull
    public Location.StationType[] getTypes() {
        return types;
    }

    public int getFrequency() {
        return frequency;
    }
}
