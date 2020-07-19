package ch.unstable.ost.api


import java.io.IOException

import ch.unstable.ost.api.model.Station

interface StationsDAO {
    @Throws(IOException::class)
    fun getStationsByQuery(query: String, types: List<Station.StationType> = listOf()): List<Station>
}
