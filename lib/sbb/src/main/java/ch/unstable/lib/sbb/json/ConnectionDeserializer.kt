package ch.unstable.lib.sbb.json

import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ConnectionDeserializer: JsonDeserializer<Connection> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Connection {
        val sections = context.deserialize<List<Section>>(json.asJsonObject["verbindungSections"], object : TypeToken<List<Section>>() {}.type)
        return Connection(sections)
    }
}