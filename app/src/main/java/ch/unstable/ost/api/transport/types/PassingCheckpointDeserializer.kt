package ch.unstable.ost.api.transport.types

import android.util.Log
import ch.unstable.ost.api.model.PassingCheckpoint
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*


const val TAG = "PassCheckpointDsrlr"
object PassingCheckpointDeserializer : JsonDeserializer<PassingCheckpoint> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PassingCheckpoint {
        val passObj = json.asJsonObject
        val location = LocationDeserializer.INSTANCE.deserialize(passObj.get("station"), null, context)

        var arrival = getNullableDate(passObj, "arrivalTimestamp")
        if (arrival == null) {
            Log.w(TAG, "arrivalTimestamp is null: " + passObj.toString())
        }
        var departure = getNullableDate(passObj, "departureTimestamp")
        if (departure == null) {
            Log.w(TAG, "departureTime is null: " + passObj.toString())
        }
        if (arrival == null && departure != null) {
            arrival = departure
        } else if (arrival != null && departure == null) {
            departure = arrival
        } else if (arrival == null) {
            Log.e(TAG, "departure and arrival is null")
            departure = Date(0)
            arrival = departure
        }
        val platform = getPlatform(passObj)
        return PassingCheckpoint(arrival, departure, location, platform)
    }
}
