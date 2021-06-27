package ch.unstable.lib.sbb.json

import com.google.gson.JsonElement
import java.text.SimpleDateFormat
import java.util.*

internal val JsonElement.nullable: JsonElement?
    get() {
        return when {
            isJsonNull -> null
            else -> this
        }
    }

internal fun readDateTime(date: JsonElement, time: JsonElement): Date {
    return readDateTime(date.asString, time.asString)
}

internal fun readDateTime(date: String, time: String): Date =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).parse("$date $time")!!
