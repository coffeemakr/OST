package ch.unstable.sbb.api.json

import ch.unstable.ost.api.sbb.model.StationResponse
import ch.unstable.sbb.api.model.Station
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
        val type = genericType<List<Station>>()
        return StationResponse(
                stations = context.deserialize(json.asJsonObject.get("standorte"), type))
    }
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type!!