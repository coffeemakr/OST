package ch.unstable.ost.views.lists.connection

import android.content.Context
import android.view.View
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.api.model.SectionType
import ch.unstable.ost.utils.TimeDateUtils

object ConnectionBinder {
    private const val TAG = "ConnectionBinder"
    private fun getTravelTimes(sections: List<Section>): List<TravelDurations> {
        val times = mutableListOf<TravelDurations>()
        var lastEnd: Long = 0
        for (section in sections) {
            // Walks can be ignored. They are added to the waiting time.
            if (lastEnd != 0L) {
                // Waiting time
                times.add(TravelDurations(
                        (section.departure.time.time - lastEnd).toUInt(),
                        TravelDurations.Type.WAIT
                ))

            }
            // Travel time
            lastEnd = section.arrival.time.time
            val type: TravelDurations.Type = when(section.type) {
                SectionType.TRANSPORT -> TravelDurations.Type.TRAVEL
                SectionType.WALK -> TravelDurations.Type.WALK
            }
            times.add(TravelDurations(
                    (section.departure.time.time - lastEnd).toUInt(),
                    type
            ))
        }
        return times
    }

    @JvmStatic
    fun bindConnection(connection: Connection, holder: ConnectionViewHolder) {
        val context = holder.itemView.context
        val firstNonWalkSection = connection.sections.firstOrNull { it.type == SectionType.TRANSPORT }
        if (firstNonWalkSection != null) {
            holder.direction.text = formatEndDestination(context, firstNonWalkSection.transportInfo?.direction)
            holder.firstTransportName.text = firstNonWalkSection.transportInfo?.shortDisplayName
            holder.platform.text = formatPlatform(context, firstNonWalkSection.departure.platform)
            if(firstNonWalkSection.departure.station.name != connection.sections.first().departure.station.name) {
                // A walk to another station is required
                holder.differentStartStation.text = firstNonWalkSection.departure.station.name
                holder.differentStartStation.visibility = View.VISIBLE
            } else {
                holder.differentStartStation.visibility = View.GONE
            }
        } else {
            error("No sections")
        }

        val duration = TimeDateUtils.formatDuration(holder.itemView.resources,
                connection.departure.time,
                connection.arrivalDate)
        holder.duration.text = duration
        holder.startTime.text = TimeDateUtils.formatTime(connection.departure.time)
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