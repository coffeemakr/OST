package ch.unstable.lib.sbb

import ch.unstable.lib.sbb.model.SbbConnectionPage
import ch.unstable.lib.sbb.model.StationResponse
import ch.unstable.ost.api.ConnectionAPI
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.model.ConnectionPage
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.api.model.Station
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class SbbApi(
        private val client: OkHttpClient,
        val baseUrl: String,
        val converter: Gson,
        val language: String = "de-de"
) : ConnectionAPI, StationsDAO {

    private val fahrplanServiceBaseUrl: String = "$baseUrl/unauth/fahrplanservice"

    private inline fun <reified T> call(request: Request): T {
        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            error("Call failed: ${response.code}")
        }
        val reader = response.body?.charStream() ?: error("No response")
        return converter.fromJson<T>(reader, T::class.java)
    }

    private fun get(url: String) = Request.Builder()
                .get()
                .addHeader("Accept-Language", language)
                .url(url)
                .build()


    override fun getConnections(connectionQuery: ConnectionQuery): ConnectionPage {
        val date: String
        val time: String
        val direction: String
        val usedDate: Date

        when {
            connectionQuery.isNow -> {
                usedDate = Date()
                direction = DIRECTION_DEPARTURE
            }
            connectionQuery.departureTime != null -> {
                usedDate = connectionQuery.departureTime!!
                direction = DIRECTION_DEPARTURE
            }
            connectionQuery.arrivalTime != null -> {
                usedDate = connectionQuery.arrivalTime!!
                direction = DIRECTION_ARRIVAL
            }
            else -> {
                error("unknown state")
            }
        }

        date = formatDate(usedDate)
        time = SimpleDateFormat("HH-mm", Locale.US).format(usedDate)
        val from = urlEncodePathSegment(connectionQuery.from)
        val to = urlEncodePathSegment(connectionQuery.to)

        val url = "$fahrplanServiceBaseUrl/v1/verbindungen/s/$from/s/$to/$direction/$date/$time/"

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
        val url = "$fahrplanServiceBaseUrl/$pageUri"
        return call(get(url))
    }

    companion object {
        private const val DIRECTION_DEPARTURE = "ab"
        private const val DIRECTION_ARRIVAL = "an"
    }

    override fun getStationsByQuery(query: String, types: List<Station.StationType>): List<Station> {
        val encodedQuery = urlEncodePathSegment(query)
        val url = "$fahrplanServiceBaseUrl/v0/standorte/$encodedQuery/?onlyHaltestellen=false"
        return call<StationResponse>(get(url)).stations
    }
}

private inline fun encodeChar(byte: Byte) = "%${byte.toUByte().toString(16).toUpperCase(Locale.US)}"

private inline fun asString(byte: Byte) = String(arrayOf(byte).toByteArray())

private fun isUrlSafe(byte: Byte): Boolean {
    return when {
        'A'.toByte() <= byte && byte <= 'Z'.toByte() -> true
        'a'.toByte() <= byte && byte <= 'z'.toByte() -> true
        '0'.toByte() <= byte && byte <= '9'.toByte() -> true
        byte == '-'.toByte() -> true
        byte == '.'.toByte() -> true
        byte == '_'.toByte() -> true
        byte == '~'.toByte() -> true
        else -> false
    }
}

fun urlEncodePathSegment(string: String): String {
    return string.toByteArray(StandardCharsets.UTF_8).joinToString(separator="") {
        when {
            isUrlSafe(it) -> asString(it)
            else -> encodeChar(it)
        }
    }
}


