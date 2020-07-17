package ch.unstable.lib.sbb

import ch.unstable.lib.sbb.model.SbbConnectionPage
import ch.unstable.lib.sbb.model.SbbStation
import ch.unstable.lib.sbb.model.StationResponse
import ch.unstable.ost.api.ConnectionAPI
import ch.unstable.ost.api.model.ConnectionPage
import ch.unstable.ost.api.model.ConnectionQuery
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class SbbApi(
        private val client: OkHttpClient,
        val baseUrl: String,
        val converter: Gson
) : ConnectionAPI {

    private inline fun <reified T> call(request: Request): T {
        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            error("Call failed: ${response.code}")
        }
        val reader = response.body?.charStream() ?: error("No response")
        return converter.fromJson<T>(reader, T::class.java)
    }

    fun getStations(query: String): List<SbbStation> {
        val encodedQuery = urlEncode(query)
        val url = "$baseUrl/unauth/fahrplanservice/v0/standorte/$encodedQuery/?onlyHaltestellen=false"
        return call<StationResponse>(get(url)).stations
    }

    private fun get(url: String) = Request.Builder()
                .get()
                .url(url)
                .build()


    override fun getConnections(connectionQuery: ConnectionQuery): ConnectionPage {
        val date: String
        val time: String
        val direction: String
        val usedDate: Date

        when {
            connectionQuery.departureTime != null -> {
                usedDate = connectionQuery.departureTime!!
                direction = "ab"
            }
            connectionQuery.arrivalTime != null -> {
                usedDate = connectionQuery.arrivalTime!!
                direction = "an"
            }
            else -> {
                error("departure and arrival time not set")
            }
        }

        date = formatDate(usedDate)
        time = SimpleDateFormat("HH-mm", Locale.US).format(usedDate)
        val from = urlEncode(connectionQuery.from)
        val to = urlEncode(connectionQuery.to)

        val url = "$baseUrl/unauth/fahrplanservice/v1/verbindungen/s/$from/s/$to/$direction/$date/$time/"

        return call<SbbConnectionPage>(get(url))
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    fun getLaterPage(connectionPage: ConnectionPage) =
            getConnectionsFromUrl((connectionPage as SbbConnectionPage).laterUrl)
                    .copy(pageNumber = connectionPage.pageNumber + 1)

    fun getEarlierPage(connectionPage: ConnectionPage) =
            getConnectionsFromUrl((connectionPage as SbbConnectionPage).earlierUrl)
                    .copy(pageNumber = connectionPage.pageNumber - 1)

    private fun getConnectionsFromUrl(pageUri: String): SbbConnectionPage {
        val url = "$baseUrl/unauth/fahrplanservice/$pageUri"
        return call(get(url))
    }
}

private inline fun encodeChar(byte: Byte) = "%${byte.toUByte().toString(16).toUpperCase(Locale.US)}"

private inline fun asString(byte: Byte) = String(arrayOf(byte).toByteArray())

fun urlEncode(string: String, safeCharacter: String ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"): String {
    return string.toByteArray(StandardCharsets.UTF_8).joinToString(separator="") {
        when {
            safeCharacter.toByteArray(StandardCharsets.US_ASCII).contains(it) -> asString(it)
            else -> encodeChar(it)
        }
    }
}


