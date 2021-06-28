package ch.unstable.lib.sbb

import ch.unstable.lib.sbb.model.SbbConnectionPageWrapper
import ch.unstable.lib.sbb.model.SbbStationsResponse
import ch.unstable.lib.sbb.model.StationWrapper
import ch.unstable.ost.api.ConnectionAPI
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.model.ConnectionPage
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.api.model.Station
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class SbbApi(
        private val client: OkHttpClient,
        val baseUrl: String,
        val language: String = "de-de"
) : ConnectionAPI, StationsDAO {

    private val fahrplanServiceBaseUrl: String = "$baseUrl/unauth/fahrplanservice"

    private inline fun <reified T> call(request: Request): T {
        val classSerializer: KSerializer<T> = serializer()
        val response = client.newCall(request).execute()
        if(!response.isSuccessful) {
            error("Call failed: ${response.code}")
        }
        val reader = response.body?.charStream() ?: error("No response")
        val jsonText = reader.readText()
        return Json {
            ignoreUnknownKeys = true
        }.decodeFromString(classSerializer, jsonText)
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

        return SbbConnectionPageWrapper.fromRaw(0, call(get(url)))
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    fun getLaterPage(connectionPage: ConnectionPage) =
            getConnectionsFromUrl((connectionPage as SbbConnectionPageWrapper).laterUrl)
                    .copy(pageNumber = connectionPage.pageNumber + 1)

    fun getEarlierPage(connectionPage: ConnectionPage) =
            getConnectionsFromUrl((connectionPage as SbbConnectionPageWrapper).earlierUrl)
                    .copy(pageNumber = connectionPage.pageNumber - 1)

    private fun getConnectionsFromUrl(pageUri: String): SbbConnectionPageWrapper {
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
        return call<SbbStationsResponse>(get(url)).standorte.map {
            StationWrapper.fromRaw(it)
        }
    }
}

private inline fun encodeChar(byte: Byte) = "%${byte.toUByte().toString(16).toUpperCase(Locale.US)}"

private inline fun asString(byte: Byte) = String(arrayOf(byte).toByteArray())

private fun isUrlSafe(byte: Byte): Boolean {
    return when {
        'A'.code.toByte() <= byte && byte <= 'Z'.code.toByte() -> true
        'a'.code.toByte() <= byte && byte <= 'z'.code.toByte() -> true
        '0'.code.toByte() <= byte && byte <= '9'.code.toByte() -> true
        byte == '-'.code.toByte() -> true
        byte == '.'.code.toByte() -> true
        byte == '_'.code.toByte() -> true
        byte == '~'.code.toByte() -> true
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


