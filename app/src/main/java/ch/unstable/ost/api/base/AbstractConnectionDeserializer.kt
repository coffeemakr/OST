package ch.unstable.ost.api.base

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type

import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section


abstract class AbstractConnectionDeserializer : JsonDeserializer<Connection> {
    abstract val sectionsField: String

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Connection {
        val sectionsArray = json.asJsonObject.get(sectionsField).asJsonArray!!
        val sections: List<Section> = context.deserialize(sectionsArray, SectionListDeserializer.type)!!
        return Connection(sections)
    }
}
