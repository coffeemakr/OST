package ch.unstable.ost.utils

import android.content.Context
import android.content.res.Resources
import android.widget.TextView
import ch.unstable.ost.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeDateUtils {
    private const val SECONDS: Long = 1000
    private const val MINUTES = 60 * SECONDS
    private const val HOURS = 60 * MINUTES
    private const val DAYS = HOURS * 24
    fun formatTime(date: Date?): String {
        val format: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun setStationStay(stationTime: TextView, arrival: Date?, departure: Date?) {
        stationTime.text = formatStationStay(stationTime.resources, arrival, departure)
    }

    fun formatStationStay(resources: Resources, arrival: Date?, departure: Date?): String? {
        return if (arrival == null && departure == null) {
            null
        } else if (arrival != null && departure != null) {
            resources.getString(R.string.station_stay_format, formatTime(arrival), formatTime(departure))
        } else if (arrival != null) {
            formatTime(arrival)
        } else {
            formatTime(departure)
        }
    }

    /**
     * Formats a duration.
     * @param resources the resources to get the strings from
     * @param start the start time
     * @param end the end time
     * @return the duration
     */
    fun formatDuration(resources: Resources, start: Date, end: Date): String {
        return formatDuration(resources, end.time - start.time)
    }

    private fun formatDuration(resources: Resources, durationMillies: Long): String {
        return if (durationMillies >= DAYS) {
            resources.getString(R.string.duration_format_days, durationMillies / DAYS, durationMillies % DAYS / HOURS, durationMillies % HOURS / MINUTES)
        } else if (durationMillies >= HOURS) {
            resources.getString(R.string.duration_format_hours, durationMillies / HOURS, durationMillies % HOURS / MINUTES)
        } else {
            resources.getString(R.string.duration_format_minutes, durationMillies / MINUTES)
        }
    }

    @JvmStatic
    fun formatDate(context: Context?, date: Date?): String {
        return android.text.format.DateFormat.getDateFormat(context).format(date)
    }
}