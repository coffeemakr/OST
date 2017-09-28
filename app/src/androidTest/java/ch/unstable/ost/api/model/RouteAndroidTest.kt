package ch.unstable.ost.api.model

import android.support.test.runner.AndroidJUnit4
import ch.unstable.ost.test.generatePassingCheckpoints

import org.junit.Test
import org.junit.runner.RunWith

import java.util.Date

import ch.unstable.ost.test.writeAndRead

import org.junit.Assert.*

fun generatePassingCheckpoint(identifier: Int): PassingCheckpoint {
    val arrival = Date(identifier.toLong())
    val departure = Date(identifier.toLong())
    val location = Location("" + identifier, "" + identifier, Location.StationType.TRAIN)
    return PassingCheckpoint(
            arrival,
            departure,
            location,
            "" + identifier)
}



@RunWith(AndroidJUnit4::class)
class RouteAndroidTest {

    @Test
    fun testParcelable() {
        val route: Route
        val shortName = "short name"
        val longName = "long name"
        val stops = generatePassingCheckpoints(20)
        route = Route(shortName, longName, stops)

        val readRoute = writeAndRead(route, Route.CREATOR)
        assertEquals(longName, readRoute.longName)
        assertEquals(shortName, readRoute.shortName)
        assertEquals(stops, readRoute.stops)
        assertEquals(readRoute, route)

    }
}