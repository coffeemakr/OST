package ch.unstable.lib.sbb.json

import android.util.Log
import ch.unstable.ost.api.model.Station
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class StationDeserializer: JsonDeserializer<Station> {
    private val tag: String =  "StationDeserializer"

    override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): Station {
        val obj = json.asJsonObject!!

        return Station(
            name = obj["displayName"].asString!!,
            id = obj["externalId"].nullable?.asString,
            type = convertToType(obj["type"].asString)
        )
    }
}

private fun convertToType(stringType: String): Station.StationType {
    return when(stringType) {
        "STATION" -> Station.StationType.TRAIN
        "POI" -> Station.StationType.POI
        "ADDRESS" -> Station.StationType.ADDRESS
        else -> {
            Log.w("convertToType", "Unknown type: $stringType")
            Station.StationType.UNKNOWN
        }
    }
}