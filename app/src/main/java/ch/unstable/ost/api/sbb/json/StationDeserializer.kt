package ch.unstable.sbb.api.json

import android.util.Log
import ch.unstable.ost.api.model.Location
import ch.unstable.sbb.api.model.Station
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

        val typeString = obj.get("type").asString!!
        val type = when(typeString) {
            "STATION" -> Location.StationType.TRAIN
            "POI" -> Location.StationType.POI
            "ADDRESS" -> Location.StationType.ADDRESS
            else -> {
                Log.w(tag, "Unknown type: " + typeString)
                Location.StationType.UNKNOWN
            }
        }

        return Station(
            displayName = obj.get("displayName").asString!!,
            externalId = obj.get("externalId").nullable?.asString,
            longitude = obj.get("longitude").asLong,
            latitude = obj.get("latitude").asLong,
            type = type
        )
    }
}

private val JsonElement.nullable: JsonElement?
    get() {
        return when {
            isJsonNull -> null
            else -> this
        }
    }