package ch.unstable.ost.api

import ch.unstable.ost.api.model.Station
import ch.unstable.ost.api.search.SearchAPI
import ch.unstable.ost.api.transport.TransportAPI
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class StationsDAOTest(private val stationsDAO: StationsDAO) {
    /*
        if(!(stationsDAO instanceof TransportAPI)) {
            assertEquals(location.getName(), Location.StationType.TRAIN, location.getType());
        }
        */
    /*
    if(!(stationsDAO instanceof TransportAPI)) {
        assertEquals(Location.StationType.BUS, location.getType());
    }
    */
    @get:Throws(Exception::class)
    @get:Test
    val stationsByQuery: Unit
        get() {
            var station: Station
            val stations: Array<Station?> = stationsDAO.getStationsByQuery("luz")
            assertDoesNotContain(null, stations)
            station = findLocationByName(stations, "Luzern")
            Assert.assertEquals("8505000", station.id)
            /*
        if(!(stationsDAO instanceof TransportAPI)) {
            assertEquals(location.getName(), Location.StationType.TRAIN, location.getType());
        }
        */Assert.assertEquals("Luzern is not first place", stations[0], station)
            station = findLocationByName(stations, "Luzern, Kantonalbank")
            Assert.assertEquals("8589801", station.id)
            /*
        if(!(stationsDAO instanceof TransportAPI)) {
            assertEquals(Location.StationType.BUS, location.getType());
        }
        */
        }

    companion object {
        @Parameterized.Parameters
        fun instancesToTest(): Collection<Array<Any>> {
            return Arrays.asList(arrayOf(SearchAPI()), arrayOf(TransportAPI()))
        }

        fun <E> assertDoesNotContain(itemNotToContain: E, items: Array<E>) {
            for (item in items) {
                if (item == itemNotToContain) {
                    Assert.fail("Array does contain item $itemNotToContain")
                }
            }
        }

        private fun findLocationByName(stations: Array<Station>, name: String): Station {
            for (location in stations) {
                if (location.name == name) {
                    return location
                }
            }
            throw AssertionError("locations don't contain location with name $name")
        }
    }

}