package ch.unstable.ost.views.lists.connection

import android.content.Context
import android.util.Log

import java.util.Arrays

import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.utils.TimeDateUtils


const private val TAG = "ConnectionBinder"

private fun getTravelTimes(sections: Collection<Section>): IntArray {
    val times = IntArray(sections.size * 2 - 1)
    var i = 0
    var lastEnd: Long = 0
    for (section in sections) {
        // Walks can be ignored. They are added to the waiting time.
        if (lastEnd != 0L) {
            // Waiting time
            times[i] = (section.departureDate.time - lastEnd).toInt()
            ++i
        }
        // Travel time
        lastEnd = section.arrivalDate.time
        times[i] = (lastEnd - section.departureDate.time).toInt()
        ++i
    }
    return if (i != times.size) {
        Arrays.copyOf(times, i)
    } else {
        times
    }
}

fun bindConnection(connection: Connection, holder: ConnectionViewHolder) {
    val context = holder.itemView.context
    val sections = connection.sections
    if (!sections.isEmpty()) {
        val firstSection = sections[0]
        holder.firstEndDestination.text = formatEndDestination(context, firstSection.headsign)
        holder.firstTransportName.text = firstSection.lineShortName
        holder.platform.text = formatPlatform(context, firstSection.departurePlatform)
    } else {
        Log.e(TAG, "No sections")
    }


    val duration = TimeDateUtils.formatDuration(holder.itemView.resources,
            connection.departureDate,
            connection.arrivalDate)
    holder.duration.text = duration
    holder.startTime.text = TimeDateUtils.formatTime(connection.departureDate)
    holder.endTime.text = TimeDateUtils.formatTime(connection.arrivalDate)

    val times = getTravelTimes(connection.sections)
    holder.connectionLineView.setLengths(times)
}

private fun isTrainPlatform(platform: String) = platform.matches("^[0-9]+$".toRegex())

private fun isBusPlatform(platform: String) = platform.matches("^[A-z]+$".toRegex())


private fun formatPlatform(context: Context, platform: String?): String? {
    return when {
        platform == null -> null
        isTrainPlatform(platform) -> context.getString(R.string.format_train_platform, platform)
        isBusPlatform(platform) -> context.getString(R.string.format_bus_platform, platform)
        else -> platform
    }
}

private fun formatEndDestination(context: Context, endDestination: String?): String? {
    return if (endDestination == null) null else context.getString(R.string.connection_direction, endDestination)
}
