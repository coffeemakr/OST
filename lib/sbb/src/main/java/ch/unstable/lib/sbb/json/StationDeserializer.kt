package ch.unstable.lib.sbb.json

import ch.unstable.lib.sbb.model.Station
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
            displayName = obj.get("displayName").asString!!,
            externalId = obj.get("externalId").nullable?.asString,
            longitude = obj.get("longitude").asLong,
            latitude = obj.get("latitude").asLong,
            type = obj.get("type").asString!!
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