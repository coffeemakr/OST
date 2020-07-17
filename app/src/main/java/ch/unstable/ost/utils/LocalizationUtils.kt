package ch.unstable.ost.utils

import android.content.Context
import androidx.annotation.StringRes
import ch.unstable.ost.R
import com.google.common.base.Preconditions
import java.util.*

object LocalizationUtils {
    /**
     * Format a departure time
     * @param context the context to get the strings from
     * @param departureTime the departure time
     * @return the departure time as a string
     */
    @JvmStatic
    fun getDepartureText(context: Context, departureTime: Date): String {
        return getSameOrOtherDayString(context, departureTime, R.string.departure_time_same_day, R.string.departure_time_other_day)
    }

    /**
     * Format a arrival time
     * @param context the context to get the strings from
     * @param arrivalTime the arrival time
     * @return the arrival time as a string
     */
    private fun getArrivalText(context: Context, arrivalTime: Date): String {
        return getSameOrOtherDayString(context, arrivalTime, R.string.arrival_time_same_day, R.string.arrival_time_other_day)
    }

    /**
     * Get a text describing the time restrictions of a query.
     *
     * If no time restrictions are given the text for "now" is returned. If both the arrival time
     * and the departure time are provided, the departure time will be used.
     *
     * @param context the context to get the strings from
     * @param arrivalTime the arrival time restrictions
     * @param departureTime the departure time restrictions
     * @return the message describing the time restrictions
     */
    @JvmStatic
    fun getArrivalOrDepartureText(context: Context, arrivalTime: Date?, departureTime: Date?): String =
            when {
                departureTime != null -> getDepartureText(context, departureTime)
                arrivalTime != null -> getArrivalText(context, arrivalTime)
                else -> context.getString(R.string.departure_time_now)
            }

    /**
     * Get a text describing the date and time
     * @param context the context to get the string from
     * @param date the date to describe
     * @param sameDayFormat the string resource which is used to format the time
     * @param otherDayFormat the string resource which is used to contain the date and the time
     * @return the text
     */
    private fun getSameOrOtherDayString(context: Context, date: Date, @StringRes sameDayFormat: Int, @StringRes otherDayFormat: Int): String {
        val today = Date()
        return if (isSameDay(today, date)) {
            context.getString(sameDayFormat, TimeDateUtils.formatTime(date))
        } else {
            context.getString(otherDayFormat, TimeDateUtils.formatTime(date), TimeDateUtils.formatDate(context, date))
        }
    }

    /**
     * Check if two dates are on the same day
     * @param first the first date
     * @param second the second date
     * @return true if the dates are on the same day
     */
    private fun isSameDay(first: Date, second: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = first
        cal2.time = second
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }
}
