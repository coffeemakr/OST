package ch.unstable.lib.sbb

import android.annotation.SuppressLint
import ch.unstable.ost.api.model.ConnectionQuery
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun createTrustAllX509TrustManager(): SbbApiFactory.SSLConfig {
    val sslContext = SSLContext.getInstance("TLS")
    val allTrustManager = @SuppressLint("TrustAllX509TrustManager") object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
        }

        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
    }
    sslContext.init(null, arrayOf<TrustManager>(allTrustManager), null)
    return SbbApiFactory.SSLConfig(sslContext.socketFactory, allTrustManager)
}

class SbbApiTest {

    @Before
    fun setup() {

    }

    lateinit var sbbApi: SbbApi

    @Before
    fun setUp() {
        sbbApi = SbbApiFactory().createAPI(createTrustAllX509TrustManager())
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


    @Test
    fun testGetLocation() {
        val locations: List<SbbStation> = sbbApi.getStations("luz")
        assertNotNull(locations)
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