package ch.unstable.ost.api.sbb

import android.util.Log
import ch.unstable.lib.sbb.SbbApi
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.model.Location

class SbbStationDao(private val api: SbbApi): StationsDAO {

    private fun convertToType(stringType: String): Location.StationType {
        return when(stringType) {
            "STATION" -> Location.StationType.TRAIN
            "POI" -> Location.StationType.POI
            "ADDRESS" -> Location.StationType.ADDRESS
            else -> {
                Log.w(TAG, "Unknown type: $stringType")
                Location.StationType.UNKNOWN
            }
        }
    }

    override fun getStationsByQuery(query: String): Array<Location> {
        return api.getStations(query)
                .map { station -> Location(
                        name = station.displayName,
                        type = convertToType(station.type),
                        id = station.externalId
                ) }.toTypedArray()
    }

    override fun getStationsByQuery(query: String, types: Array<Location.StationType>?): Array<Location> {
        return getStationsByQuery(query)
    }

    companion object {
        private val TAG = "SbbStationDao"
    }
}