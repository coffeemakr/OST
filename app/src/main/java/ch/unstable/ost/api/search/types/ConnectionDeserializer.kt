package ch.unstable.ost.api.search.types

import ch.unstable.ost.api.base.SectionListDeserializer
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import com.google.gson.*
import java.lang.reflect.Type

object ConnectionDeserializer : JsonDeserializer<Connection> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Connection {
        val connectionObj :JsonObject = json.asJsonObject
        val sections: List<Section> = context.deserialize(connectionObj.get("legs"), SectionListDeserializer.type)
        return Connection(sections)
    }
}