package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ConnectionQuery(
        /**
         * Get the departure location
         *
         * @return the departure location
         */
        val from: String,
        /**
         * Get the name of the arrival location
         *
         * @return the arrival location
         */
        val to: String,
        /**
         * Get array containing the via stations in order.
         *
         * @return the stations
         */
        val via: List<String> = listOf(),
        /**
         * Get the departure time
         *
         *
         * If the query has a arrival time or is for now this method returns null.
         *
         * @return the departure time or null
         */
        val departureTime: Date? = null,
        val arrivalTime: Date? = null) : Parcelable {

    /**
     * Check if the query has vias
     *
     * @return true if the query has vias. False otherwise
     */
    val hasVias: Boolean
        get() = via.isNotEmpty()

    /**
     * Check if the query is for the current time
     *
     * @return true if it is a query for now
     */
    val isNow: Boolean
        get() = departureTime == null && arrivalTime == null

}