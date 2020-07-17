package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * TODO: check if the stops contain the arrival and departure
 */
@Parcelize
data class Section(
        val departure: DepartureCheckpoint,
        val arrival: ArrivalCheckpoint,
        val headsign: String,
        private val walkTime: Long,
        private val route: Route) : Parcelable {

    val lineShortName: String
        get() = route.shortName

    val departureDate: Date
        get() = departure.departureTime

    val arrivalDate: Date
        get() = arrival.arrivalTime

    val arrivalLocation: Location
        get() = arrival.location

    val departureLocation: Location
        get() = departure.location

    val departurePlatform: String
        get() = departure.platform

    val arrivalPlatform: String
        get() = arrival.platform

    val stops: List<PassingCheckpoint>
        get() = route.stops

    val routeLongName: String
        get() = route.longName
}