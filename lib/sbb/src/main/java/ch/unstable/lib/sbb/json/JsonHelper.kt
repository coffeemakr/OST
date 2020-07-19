package ch.unstable.lib.sbb.json

import com.google.gson.JsonElement

val JsonElement.nullable: JsonElement?
    get() {
        return when {
            isJsonNull -> null
            else -> this
        }
    }