package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Create a new connection
 *
 * @param sections an array containing the sections
 */
@Parcelize
data class Connection(val sections: List<Section>) : Parcelable {

    /**
     * Get the departure time
     *
     * @return the time of the departure
     */
    val departureDate: Date
        get() = sections.first().departureDate

    /**
     * Get the arrival time
     *
     * @return the arrival time
     */
    val arrivalDate: Date
        get() = sections.last().arrivalDate

    /**
     * Get the departure checkpoint
     *
     *
     * The departure checkpoint of a connection is the departure checkpoint of the first section.
     *
     * @return the departure checkpoint
     */
    val departure: DepartureCheckpoint
        get() = sections.first().departure

    /**
     * Get the arrival checkpoint
     *
     *
     * The arrival checkpoint of a connection is the arrival checkpoint of the last section.
     *
     * @return the arrival checkpoint
     */
    val arrival: ArrivalCheckpoint
        get() = sections.last().arrival

    val departureName: String
        get() = departure.location.name

    val arrivalName: String
        get() = arrival.location.name

}