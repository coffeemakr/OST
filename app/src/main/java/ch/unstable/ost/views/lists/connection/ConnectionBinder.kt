package ch.unstable.ost.views.lists.connection

import android.content.Context
import android.util.Log
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.utils.TimeDateUtils
import java.util.*

object ConnectionBinder {
    private const val TAG = "ConnectionBinder"
    private fun getTravelTimes(sections: List<Section>): IntArray {
        var times = IntArray(sections.size * 2 - 1)
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
        if (i != times.size) {
            times = times.copyOf(i)
        }
        return times
    }

    @JvmStatic
    fun bindConnection(connection: Connection, holder: ConnectionViewHolder) {
        val context = holder.itemView.context
        val sections: Array<Section> = connection.sections.toTypedArray()
        if (sections.isNotEmpty()) {
            val section = sections[0]
            holder.firstEndDestination.text = formatEndDestination(context, section.headsign)
            holder.firstTransportName.text = section.lineShortName
            holder.platform.text = formatPlatform(context, section.departurePlatform)
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

    private fun formatPlatform(context: Context, platform: String?): String? {
        return when {
            platform == null -> null
            platform.matches(Regex("^[0-9]+$")) -> {
                context.getString(R.string.format_train_platform, platform)
            }
            platform.matches(Regex("^[A-z]+$")) -> {
                context.getString(R.string.format_bus_platform, platform)
            }
            else -> {
                platform
            }
        }
    }

    private fun formatEndDestination(context: Context, endDestination: String?): String? {
        return if (endDestination == null) null else context.getString(R.string.connection_direction, endDestination)
    }
}