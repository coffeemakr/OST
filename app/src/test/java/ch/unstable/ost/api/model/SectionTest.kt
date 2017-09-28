package ch.unstable.ost.api.model

import ch.unstable.ost.test.generateArrivalCheckpoint
import ch.unstable.ost.test.generateDepartureCheckpoint
import ch.unstable.ost.test.generateRandomRoute
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.*


class SectionTest {

    @Test
    fun describeContents() {
        val section = Section(VALID_ROUTE, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(0, section.describeContents().toLong())
    }

    @Test
    fun getLineShortName() {
        val shortName = "Short Name"
        val longName = "Long Name"
        val stops = generatePassingCheckpoints(213)
        val route = Route(shortName, longName, stops)
        val section = Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(shortName, section.lineShortName)
    }

    @Test
    fun getRouteLongName() {
        val shortName = "Short Name"
        val longName = "Long Name"
        val stops = generatePassingCheckpoints(0)
        val route = Route(shortName, longName, stops)
        val section = Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(longName, section.routeLongName)
    }

    @Test
    fun getRouteStops() {
        val shortName = "Short Name"
        val longName = "Long Name"
        val stops = generatePassingCheckpoints(100)
        val route = Route(shortName, longName, stops)
        val section = Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertNotSame(stops, section.stops)
        assertEquals(stops, section.stops)
    }


    @Test
    fun getDeparture() {
        val departureTime = Date()
        val location = mock(Location::class.java)
        val departureCheckpoint = DepartureCheckpoint(departureTime, "21", location)
        val section = Section(VALID_ROUTE, departureCheckpoint, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(departureTime, section.departureDate)
        assertEquals(location, section.departureLocation)
        assertEquals("21", section.departurePlatform)
        assertSame(departureCheckpoint, section.departure)

    }

    @Test
    fun getArrival() {
        val arrivalTime = Date()
        val location = mock(Location::class.java)
        val arrivalCheckpoint = ArrivalCheckpoint(arrivalTime, "21", location)
        val section = Section(VALID_ROUTE, VALID_DEPARTURE, arrivalCheckpoint, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(arrivalTime, section.arrivalDate)
        assertEquals(location, section.arrivalLocation)
        assertEquals("21", section.arrivalPlatform)
        assertSame(arrivalCheckpoint, section.arrival)
    }

    @Test
    fun getHeadsign() {
        val section = Section(VALID_ROUTE, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME)
        assertEquals(VALID_HEADSIGN, section.headsign)
    }

    companion object {

        private val VALID_ROUTE = generateRandomRoute(321)
        private val VALID_DEPARTURE = generateDepartureCheckpoint(12)
        private val VALID_ARRIVAL = generateArrivalCheckpoint(94)
        private val VALID_HEADSIGN = "Head"
        private val VALID_WALKTIME: Long = 1000
    }
}