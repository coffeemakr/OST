package ch.unstable.ost.api.transport.types

import ch.unstable.ost.api.model.ArrivalCheckpoint
import ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer.getDate
import ch.unstable.ost.api.transport.types.DepartureCheckpointDeserializer.getPlatform
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

object ArrivalStopDeserializer : JsonDeserializer<ArrivalCheckpoint> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ArrivalCheckpoint {
        val passObj = json.asJsonObject
        val location = LocationDeserializer.INSTANCE.deserialize(passObj.get("station"), null, context)

        val arrival = getDate(passObj, "arrivalTimestamp")
        val platform = getPlatform(passObj)
        return ArrivalCheckpoint(arrival, platform, location)
    }
}
