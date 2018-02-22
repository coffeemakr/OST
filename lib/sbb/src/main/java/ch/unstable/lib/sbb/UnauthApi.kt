package ch.unstable.lib.sbb

import ch.unstable.lib.sbb.model.StationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UnauthApi {
    @GET("/unauth/fahrplanservice/v0/standorte/{query}/?onlyHaltestellen=false")
    fun getStations(@Path("query") query: String): Call<StationResponse>
}
