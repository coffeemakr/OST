package ch.unstable.ost.api.offline;

import android.arch.persistence.room.TypeConverter;


public class StationTypeConverter {

    @TypeConverter
    public ch.unstable.ost.api.model.impl.Location.StationType[] getStationTypesFromInteger(int mask) {
        return ch.unstable.ost.api.model.impl.Location.StationType.fromMask(mask);
    }

    @TypeConverter
    public int getIntegerFromStationTypes(ch.unstable.ost.api.model.impl.Location.StationType[] types) {
        return ch.unstable.ost.api.model.impl.Location.StationType.getMask(types);
    }
}
