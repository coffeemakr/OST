package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.utils.ParcelUtils
import ch.unstable.ost.utils.ParcelUtils.*

import com.google.common.base.Objects

import java.util.Date

import com.google.common.base.Preconditions.checkNotNull

/**
 * TODO: check if the stops contain the arrival and departure
 */
data class Section(val route: Route,
                   val departure: DepartureCheckpoint,
                   val arrival: ArrivalCheckpoint,
                   val headsign: String,
                   val walkTime: Long?) : Parcelable {

    val lineShortName: String?
        get() = route.shortName

    val departureDate: Date
        get() = departure.departureTime

    val arrivalDate: Date
        get() = arrival.arrivalTime

    val arrivalLocation: Location
        get() = arrival.location

    val departureLocation: Location
        get() = departure.location

    val departurePlatform: String?
        get() = departure.platform

    val arrivalPlatform: String?
        get() = arrival.platform

    val stops: List<PassingCheckpoint>
        get() = route.stops

    val routeLongName: String
        get() = route.longName

    private constructor(`in`: Parcel): this(
        route = readNonNulTypedObject(`in`, Route.CREATOR),
        departure = readNonNulTypedObject(`in`, DepartureCheckpoint.CREATOR),
        arrival = readNonNulTypedObject(`in`, ArrivalCheckpoint),
        headsign = `in`.readString(),
        walkTime = ParcelUtils.readNullableLong(`in`)!!)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeNonNullTypedObject(dest, route, flags)
        writeNonNullTypedObject(dest, departure, flags)
        writeNonNullTypedObject(dest, arrival, flags)
        dest.writeString(headsign)
        writeNullableLong(dest, walkTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Section> {
        override fun createFromParcel(`in`: Parcel): Section {
            return Section(`in`)
        }

        override fun newArray(size: Int): Array<Section?> {
            return arrayOfNulls(size);
        }
    }
}
