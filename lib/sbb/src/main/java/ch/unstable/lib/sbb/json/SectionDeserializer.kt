package ch.unstable.lib.sbb.json

import ch.unstable.ost.api.model.*
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class SectionDeserializer: JsonDeserializer<Section> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Section {
        val obj = json.asJsonObject
        val arrivalCheckpoint =readTimedCheckpoint(obj, "ankunft")
        val departureCheckpoint =readTimedCheckpoint(obj, "abfahrt")
        val type: SectionType
        val transportInfo: TransportInfo?
        when (obj["type"].asString) {
            "WALK" -> {
                type = SectionType.WALK
                transportInfo = null
            }
            "TRANSPORT" -> {
                type = SectionType.TRANSPORT
                transportInfo = context.deserialize(obj["transportBezeichnung"], TransportInfo::class.java)
            }
            else -> error("Unknown type")
        }

        return Section(
                arrival = arrivalCheckpoint,
                departure = departureCheckpoint,
                type = type,
                transportInfo=transportInfo
        )
    }

    private fun readTimedCheckpoint(obj: JsonObject, prefix: String): TimedCheckpoint {
        //val coordinatesObject = obj[prefix + "Koordinaten"].asJsonObject
        //val coordinates = Coordinates(x = coordinatesObject["latitude"].asInt, y = coordinatesObject["longitude"])
        val location = Station(name = obj[prefix + "Name"].asString,
                type = Station.StationType.UNKNOWN,
                id = null)
        val time = readDateTime(obj[prefix + "Datum"], obj[prefix + "Time"])

        val platform = obj[prefix + "Gleis"].asString
        val coordinates = obj[prefix + "Koordinaten"].asJsonObject
        return TimedCheckpoint(
                time = time,
                station = location,
                platform = platform,
                latitude = coordinates["latitude"].asLong,
                longitude = coordinates["longitude"].asLong
        )
    }

    private fun readDateTime(date: JsonElement, time: JsonElement): Date {
        return readDateTime(date.asString, time.asString)
    }

    private fun readDateTime(date: String, time: String): Date =
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).parse("$date $time")!!
}