package ch.unstable.lib.sbb.json

import java.text.SimpleDateFormat
import java.util.*

internal fun readDateTime(date: String, time: String): Date =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).parse("$date $time")!!
