package ch.unstable.ost.lists.connection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;

public class ConnectionBinder {

    private static final String TAG = "ConnectionBinder";

    private static int[] getTravelTimes(Section[] sections) {
        int[] times = new int[sections.length * 2 - 1];
        int i = 0;
        long lastEnd = 0;
        for (Section section : sections) {
            // Walks can be ignored. They are added to the waiting time.
            if (lastEnd != 0) {
                // Waiting time
                times[i] = (int) (section.getDepartureDate().getTime() - lastEnd);
                ++i;
            }
            // Travel time
            lastEnd = section.getArrivalDate().getTime();
            times[i] = (int) (lastEnd - section.getDepartureDate().getTime());
            ++i;
        }
        if (i != times.length) {
            times = Arrays.copyOf(times, i);
        }
        return times;
    }

    public static void bindConnection(Connection connection, ConnectionViewHolder holder) {
        final Context context = holder.itemView.getContext();
        Section[] sections = connection.getSections();
        if (sections.length > 0) {
            Section section = sections[0];
            holder.firstEndDestination.setText(formatEndDestination(context, section.getHeadsign()));
            holder.firstTransportName.setText(section.getLineShortName());
            holder.platform.setText(formatPlatform(context, section.getDeparturePlatform()));
        } else {
            Log.e(TAG, "No sections");
        }


        String duration = TimeDateUtils.formatDuration(holder.itemView.getResources(),
                connection.getDepartureDate(),
                connection.getArrivalDate());
        holder.duration.setText(duration);
        holder.startTime.setText(TimeDateUtils.formatTime(connection.getDepartureDate()));
        holder.endTime.setText(TimeDateUtils.formatTime(connection.getArrivalDate()));

        int[] times = getTravelTimes(connection.getSections());
        holder.connectionLineView.setLengths(times);
    }

    @Nullable
    private static String formatPlatform(Context context, @Nullable String platform) {
        if (platform == null) {
            return null;
        } else if (platform.matches("^[0-9]+$")) {
            return context.getString(R.string.format_train_platform, platform);
        } else if (platform.matches("^[A-z]+$")) {
            return context.getString(R.string.format_bus_platform, platform);
        } else {
            return platform;
        }
    }

    @Nullable
    private static String formatEndDestination(Context context, @Nullable String endDestination) {
        if (endDestination == null) return null;
        return context.getString(R.string.connection_direction, endDestination);
    }
}
