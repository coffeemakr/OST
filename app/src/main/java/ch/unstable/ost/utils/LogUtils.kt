package ch.unstable.ost.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

object LogUtils {
    /**
     * Get a pretty json from an json element.
     * @param element the json element
     * @return the pretty json
     */
    fun prettyJson(element: JsonElement?): String {
        val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
        return gson.toJson(element)
    }
}