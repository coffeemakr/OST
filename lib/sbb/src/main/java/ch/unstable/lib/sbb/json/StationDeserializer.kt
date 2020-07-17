package ch.unstable.lib.sbb.json

import ch.unstable.lib.sbb.model.SbbStation
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class StationDeserializer: JsonDeserializer<SbbStation> {
    private val tag: String =  "StationDeserializer"

    override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): SbbStation {
        val obj = json.asJsonObject!!

        return SbbStation(
            displayName = obj["displayName"].asString!!,
            externalId = obj["externalId"].nullable?.asString,
            longitude = obj["longitude"].asLong,
            latitude = obj["latitude"].asLong,
            type = obj["type"].asString!!
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