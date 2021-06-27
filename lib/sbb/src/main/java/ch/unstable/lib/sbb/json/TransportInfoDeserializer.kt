package ch.unstable.lib.sbb.json

import ch.unstable.ost.api.model.TransportInfo
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class TransportInfoDeserializer: JsonDeserializer<TransportInfo> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): TransportInfo {
        val obj = json.asJsonObject
        // label = obj["transportLabel"].asString
        return TransportInfo(
                direction = obj["transportDirection"].asString,
                label = obj["transportLabel"].asString,
                text = obj["transportText"].asString,
                name = obj["transportText"].nullable?.asString,
                icon = obj["transportIcon"].asString,
                iconSuffix = obj["transportIconSuffix"].nullable?.asString
        )
    }
}

/*
    "oevIcon": "ZUG",
    "transportIcon": "IR",
    "transportIconSuffix": "36",
    "transportLabel": "",
    "transportText": "2075",
    "transportName": null,
    "transportDirection": "Zürich Flughafen",
    "transportLabelBgColor": null,
    "transportLabelTextColor": null
 */