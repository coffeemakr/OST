package ch.unstable.lib.sbb.json

import ch.unstable.ost.api.model.TransportInfo
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class TransportInfoDeserializer: JsonDeserializer<TransportInfo> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): TransportInfo {
        val obj = json.asJsonObject
        return TransportInfo(
                direction = obj["transportDirection"].asString,
                label = obj["transportLabel"].asString
        )
    }
}