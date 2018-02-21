package ch.unstable.ost.api.sbb.auth

import okhttp3.Request
import org.apache.commons.codec.binary.Hex
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
* Created on 06.01.18.
*/
class AuthInterceptorTest {
    @Test
    fun intercept() {
        // example values from https://blog.unstable.ch/security/2017/12/11/sbb-client-reverse-engineering.html
        val certHash = Hex.decodeHex("59d7e7cdd42e8111541796fcd768599779406a13")
        val calendar = GregorianCalendar()
        calendar.set(2017, 11 /* january is 0 */, 11)
        val authenticator = AuthInterceptor(certHash, CustomDateSource(Date(calendar.timeInMillis)))
        val request = Request.Builder()
                .url("https://p1.bla.ch/unauth/fahrplanservice/v0/standorte/Test/")
                .build()
        val newRequest = authenticator.createNewRequest(request)
        assertEquals("Date differs", "2017-12-11", newRequest.header("X-API-DATE"))
        assertEquals("Auth token", "Cg77W3p/UuDCWKpsLNrTX6sHgEU=", newRequest.header("X-API-AUTHORIZATION"))
    }
}