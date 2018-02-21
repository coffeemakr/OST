package ch.unstable.ost.api.sbb

import android.util.Log
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.model.Location


class SbbStationDao(private val api: UnauthApi): StationsDAO {
    private val TAG = "SbbStationDao"

    override fun getStationsByQuery(query: String): Array<Location> {
        val response = api.getStations(query).execute()
        Log.d(TAG, "response: " + response.raw())
        return response.body()?.stations
                .orEmpty()
                .map { station -> Location(
                        name = station.displayName,
                        type = station.type,
                        id = station.externalId
                ) }.toTypedArray()
    }

    override fun getStationsByQuery(query: String, types: Array<Location.StationType>?): Array<Location> {
        return getStationsByQuery(query)
    }

}