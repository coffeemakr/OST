package ch.unstable.lib.sbb.auth

import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*


class AuthInterceptorTest {
    @Test
    fun intercept() {
        val calendar = GregorianCalendar()
        calendar.set(2017, 11 /* january is 0 */, 11)
        val authenticator = AuthInterceptor(CustomDateSource(Date(calendar.timeInMillis)))
        val request = Request.Builder()
                .url("https://p1.bla.ch/unauth/fahrplanservice/v0/standorte/Test/")
                .build()
        val newRequest = authenticator.createNewRequest(request)
        assertEquals("Date differs", "2017-12-11", newRequest.header("X-API-DATE"))
        assertEquals("Auth token", "Cg77W3p/UuDCWKpsLNrTX6sHgEU=", newRequest.header("X-API-AUTHORIZATION"))
    }

    @Test
    fun intercept_umlauts() {
        val calendar = GregorianCalendar()
        calendar.set(2020, 6, 15)
        val authenticator = AuthInterceptor(CustomDateSource(Date(calendar.timeInMillis)))
        val request = Request.Builder()
                .url("https://p1.bla.ch/unauth/fahrplanservice/v1/standorte/H%C3%BCswil/?onlyHaltestellen=false")
                .build()
        val newRequest = authenticator.createNewRequest(request)
        assertEquals("URL is wrong", "https://p1.bla.ch/unauth/fahrplanservice/v1/standorte/H%25C3%25BCswil/?onlyHaltestellen=false", newRequest.url.toString())
        assertEquals("Date differs", "2020-07-15", newRequest.header("X-API-DATE"))
        assertEquals("Auth token", "iuNmY6Qyjgl32MQ6Po4wPkovncs=", newRequest.header("X-API-AUTHORIZATION"))
    }
}