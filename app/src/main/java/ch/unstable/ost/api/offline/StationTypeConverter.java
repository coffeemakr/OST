package ch.unstable.ost.api.offline;

import android.arch.persistence.room.TypeConverter;

import ch.unstable.ost.api.model.Location;


public class StationTypeConverter {

    @TypeConverter
    public Location.StationType[] getStationTypesFromInteger(int mask) {
        return Location.StationType.fromMask(mask);
    }

    @TypeConverter
    public int getIntegerFromStationTypes(Location.StationType[] types) {
        return Location.StationType.getMask(types);
    }
}
