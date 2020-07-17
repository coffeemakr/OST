package ch.unstable.lib.sbb.json

import ch.unstable.lib.sbb.model.StationResponse
import ch.unstable.lib.sbb.model.SbbStation
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StationResponseDeserializer : JsonDeserializer<StationResponse> {
    override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): StationResponse {
        val type = genericType<List<SbbStation>>()
        return StationResponse(
                stations = context.deserialize(json.asJsonObject.get("standorte"), type))
    }
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type!!