package ch.unstable.lib.sbb.json

import ch.unstable.ost.api.model.RealtimeInfo
import ch.unstable.ost.api.model.RealtimeInfoPart
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class RealtimeInfoDeserializer : JsonDeserializer<RealtimeInfo> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RealtimeInfo {
        val obj = json.asJsonObject
        val departurePart = RealtimeInfoPart(
                actualTime = readDateTime(time = obj["abfahrtIstZeit"], date = obj["abfahrtIstDatum"]),
                undefinedDelay = obj["abfahrtDelayUndefined"].asBoolean,
                cancellation = obj["abfahrtCancellation"].asBoolean,
                plattformChange = obj["abfahrtPlatformChange"].asBoolean
        )
        val arrivalPart = RealtimeInfoPart(
                actualTime = readDateTime(time = obj["ankunftIstZeit"], date = obj["ankunftIstDatum"]),
                undefinedDelay = obj["ankunftDelayUndefined"].asBoolean,
                cancellation = obj["ankunftCancellation"].asBoolean,
                plattformChange = obj["ankunftPlatformChange"].asBoolean
        )
        return RealtimeInfo(arrival = arrivalPart, departure = departurePart)
    }
}

/*
"realtimeInfo": {
"abfahrtIstZeit": "15:12",
"abfahrtIstDatum": "27.06.2021",
"ankunftIstZeit": "15:27",
"ankunftIstDatum": "27.06.2021",
"abfahrtDelayUndefined": false,
"abfahrtPlatformChange": false,
"ankunftCancellation": false,
"ankunftDelayUndefined": false,
"ankunftPlatformChange": false,
"abfahrtCancellation": false
},
*/