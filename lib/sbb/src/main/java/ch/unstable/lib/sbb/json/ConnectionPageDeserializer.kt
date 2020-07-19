package ch.unstable.lib.sbb.json

import ch.unstable.lib.sbb.model.SbbConnectionPage
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Station
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ConnectionPageDeserializer: JsonDeserializer<SbbConnectionPage> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): SbbConnectionPage {
        val obj = json.asJsonObject
        val connectionsNode = obj["verbindungen"].asJsonArray
        val start: Station = context.deserialize(obj["abfahrt"])
        val destination: Station = context.deserialize(obj["ankunft"])

        val connections: List<Connection>  = context.deserializeList(connectionsNode)
        return SbbConnectionPage(
                start = start,
                destination = destination,
                earlierUrl = obj["earlierUrl"].asString,
                laterUrl = obj["laterUrl"].asString,
                connections=connections)
    }
}

inline fun <reified T> JsonDeserializationContext.deserialize(json: JsonElement): T = deserialize<T>(json, T::class.java)


inline fun <reified T> JsonDeserializationContext.deserializeList(json: JsonElement): T =
        deserialize<T>(json, object : TypeToken<T>() {}.type)
