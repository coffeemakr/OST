package ch.unstable.ost.api


import java.io.IOException

import ch.unstable.ost.api.model.Location

interface StationsDAO {
    @Throws(IOException::class)
    fun getStationsByQuery(query: String): Array<Location>

    @Throws(IOException::class)
    fun getStationsByQuery(query: String, types: Array<Location.StationType>?): Array<Location>
}
