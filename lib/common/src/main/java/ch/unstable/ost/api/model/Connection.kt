package ch.unstable.ost.api.model

import android.os.Parcelable
import java.util.*

/**
 * Create a new connection
 *
 * @param sections an array containing the sections
 */
interface Connection : Parcelable {

    val sections: List<Section>

    /**
     * Get the departure time
     *
     * @return the time of the departure
     */
    val departureDate: Date
        get() = departure.time

    /**
     * Get the arrival time
     *
     * @return the arrival time
     */
    val arrivalDate: Date
        get() = arrival.time

    /**
     * Get the departure checkpoint
     *
     *
     * The departure checkpoint of a connection is the departure checkpoint of the first section.
     *
     * @return the departure checkpoint
     */
    val departure
        get() = sections.first().departure

    /**
     * Get the arrival checkpoint
     *
     *
     * The arrival checkpoint of a connection is the arrival checkpoint of the last section.
     *
     * @return the arrival checkpoint
     */
    val arrival
        get() = sections.last().arrival

    val departureName: String
        get() = departure.station.name

    val arrivalName: String
        get() = arrival.station.name

}