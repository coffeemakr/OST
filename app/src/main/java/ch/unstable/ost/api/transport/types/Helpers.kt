package ch.unstable.ost.api.transport.types

import com.google.gson.JsonObject
import java.util.*

private val FIELD_PLATFORM = "platform"

fun getPlatform(jsonObject: JsonObject): String? {
    if (!jsonObject.has(FIELD_PLATFORM)) {
        return null
    }
    val platform = jsonObject.get(FIELD_PLATFORM)
    return if (!platform.isJsonNull) {
        platform.asString.trim { it <= ' ' }
    } else null
}

fun getNullableDate(jsonObject: JsonObject, field: String): Date? {
    val value = jsonObject.get(field)
    return if (value.isJsonNull) {
        null
    } else {
        Date(value.asLong * 1000)
    }
}

fun getDate(obj: JsonObject, fieldName: String): Date {
    return Date(obj.get(fieldName).asLong * 1000L)
}