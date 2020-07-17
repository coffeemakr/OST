package ch.unstable.lib.sbb

import ch.unstable.ost.api.model.ConnectionQuery
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*


class SbbApiTest {

    @Before
    fun setup() {

    }

    lateinit var sbbApi: SbbApi

    @Before
    fun setUp() {
        sbbApi = SbbApiFactory().createAPI(SbbApiFactory.createTrustAllX509TrustManager())
    }

    @Test
    fun getConnection() {
        val departure = getDate(2020, 7, 15, 14, 33)
        val connectionPage = sbbApi.getConnections(ConnectionQuery(
                from = "Zürich Hardbrücke",
                to = "Zürich HB",
                departureTime = departure
        ))
        println(connectionPage)
        assertEquals(5, connectionPage.connections.size)
        assertEquals(0, connectionPage.pageNumber)

        val laterPage = sbbApi.getLaterPage(connectionPage)
        println(laterPage)
        assertEquals(5, connectionPage.connections.size)
        assertEquals(1, laterPage.pageNumber)
    }

    private fun getDate(year: Int, month: Int, day: Int): Date {
        val calendar = GregorianCalendar()
        calendar.set(year, month - 1, day)
        return Date(calendar.timeInMillis)
    }

    private fun getDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        val calendar = GregorianCalendar()
        calendar.set(year, month - 1, day, hour, minute)
        return Date(calendar.timeInMillis)
    }
}