package ch.unstable.ost.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeDateUtils {
    public static String formatTime(Date date) {
        java.text.DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }
}
