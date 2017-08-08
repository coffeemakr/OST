package ch.unstable.ost.api.offline;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ch.unstable.ost.api.offline.model.LocationEntity;
import ch.unstable.ost.api.offline.model.LocationEntityFTS;

@TypeConverters(value = {StationTypeConverter.class})
@Database(entities = {LocationEntity.class, LocationEntityFTS.class}, version = 1)
public abstract class StationsDatabase extends RoomDatabase{

    public abstract OfflineStationsDAO getStationsDAO();
}
