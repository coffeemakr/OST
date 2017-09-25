package ch.unstable.ost.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.common.base.Preconditions;

import java.util.Calendar;
import java.util.Date;

import ch.unstable.ost.R;

import static com.google.common.base.Preconditions.checkNotNull;

public enum LocalizationUtils {
    ;

    /**
     * Format a departure time
     * @param context the context to get the strings from
     * @param departureTime the departure time
     * @return the departure time as a string
     */
    @NonNull
    public static String getDepartureText(final Context context, Date departureTime) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(departureTime, "departureTime is null");
        return getSameOrOtherDayString(context, departureTime, R.string.departure_time_same_day, R.string.departure_time_other_day);
    }

    /**
     * Format a arrival time
     * @param context the context to get the strings from
     * @param arrivalTime the arrival time
     * @return the arrival time as a string
     */
    @NonNull
    private static String getArrivalText(Context context, Date arrivalTime) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(arrivalTime, "arrivalTime is null");
        return getSameOrOtherDayString(context, arrivalTime, R.string.arrival_time_same_day, R.string.arrival_time_other_day);
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
    @NonNull
    public static String getArrivalOrDepartureText(final Context context, @Nullable Date arrivalTime, @Nullable Date departureTime) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(context, "context is null");
        Date time;
        if ((time = departureTime) != null) {
            return getDepartureText(context, time);
        } else if ((time = arrivalTime) != null) {
            return getArrivalText(context, time);
        } else {
            return context.getString(R.string.departure_time_now);
        }
    }

    /**
     * Get a text describing the date and time
     * @param context the context to get the string from
     * @param date the date to describe
     * @param sameDayFormat the string resource which is used to format the time
     * @param otherDayFormat the string resource which is used to contain the date and the time
     * @return the text
     */
    @NonNull
    private static String getSameOrOtherDayString(Context context, Date date, @StringRes int sameDayFormat, @StringRes int otherDayFormat) {
        Date today = new Date();
        if (isSameDay(today, date)) {
            return context.getString(sameDayFormat, TimeDateUtils.formatTime(date));
        } else {
            return context.getString(otherDayFormat, TimeDateUtils.formatTime(date), TimeDateUtils.formatDate(context, date));
        }
    }

    /**
     * Check if two dates are on the same day
     * @param first the first date
     * @param second the second date
     * @return true if the dates are on the same day
     */
    private static boolean isSameDay(Date first, Date second) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(first);
        cal2.setTime(second);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
