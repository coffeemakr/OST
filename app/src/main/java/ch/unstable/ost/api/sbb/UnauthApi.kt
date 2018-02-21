package ch.unstable.ost.api.sbb

import ch.unstable.ost.api.sbb.model.StationResponse
import ch.unstable.sbb.api.model.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UnauthApi {
    @GET("/unauth/fahrplanservice/v0/standorte/{query}/?onlyHaltestellen=false")
    fun getStations(@Path("query") query: String): Call<StationResponse>
}
