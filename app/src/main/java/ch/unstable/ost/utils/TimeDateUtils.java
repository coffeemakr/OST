package ch.unstable.ost.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.unstable.ost.R;

public class TimeDateUtils {

    private final static long SECONDS = 1000;
    private final static long MINUTES = 60 * SECONDS;
    private final static long HOURS = 60 * MINUTES;
    private final static long DAYS = HOURS * 24;

    public static String formatTime(Date date) {
        java.text.DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }


    public static void setStationStay(TextView stationTime, @Nullable Date arrival, @Nullable Date departure) {
        stationTime.setText(formatStationStay(stationTime.getResources(), arrival, departure));
    }

    @Nullable
    public static String formatStationStay(Resources resources, @Nullable Date arrival, @Nullable Date departure) {
        if(arrival == null && departure == null) {
            return null;
        } else if(arrival != null && departure != null) {
            return resources.getString(R.string.station_stay_format, formatTime(arrival), formatTime(departure));
        } else if(arrival != null) {
            return formatTime(arrival);
        } else {
            return formatTime(departure);
        }
    }

    /**
     * Formats a duration.
     * @param resources the resources to get the strings from
     * @param start the start time
     * @param end the end time
     * @return the duration
     */
    public static String formatDuration(Resources resources, final Date start, final Date end) {
        return formatDuration(resources, end.getTime() - start.getTime());
    }


    private static String formatDuration(Resources resources, final long durationMillies) {
        if (durationMillies >= DAYS) {
            return resources.getString(R.string.duration_format_days, durationMillies / DAYS, (durationMillies % DAYS) / HOURS, (durationMillies % HOURS) / MINUTES);
        } else if (durationMillies >= HOURS) {
            return resources.getString(R.string.duration_format_hours, durationMillies / HOURS, (durationMillies % HOURS) / MINUTES);
        } else {
            return resources.getString(R.string.duration_format_minutes, durationMillies / MINUTES);
        }
    }

    public static String formatDate(Context context, Date date) {
        return DateFormat.getDateFormat(context).format(date);
    }

}
