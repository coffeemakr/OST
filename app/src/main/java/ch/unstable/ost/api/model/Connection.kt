package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.MoreObjects

import java.util.Arrays
import java.util.Date

import com.google.common.base.Preconditions.checkNotNull


/**
 * Create a new connection
 *
 * @param sections an array containing the sections
 */
data class Connection(val sections: List<Section>) : Parcelable {

    /**
     * Get the departure time
     *
     * @return the time of the departure
     */
    val departureDate: Date
        get() = sections[0].departureDate

    /**
     * Get the arrival time
     *
     * @return the arrival time
     */
    val arrivalDate: Date
        get() = sections[sections.size - 1].arrivalDate


    /**
     * Get the departure checkpoint
     *
     *
     * The departure checkpoint of a connection is the departure checkpoint of the first section.
     *
     * @return the departure checkpoint
     */
    val departure: DepartureCheckpoint
        get() = sections[0].departure

    /**
     * Get the arrival checkpoint
     *
     *
     * The arrival checkpoint of a connection is the arrival checkpoint of the last section.
     *
     * @return the arrival checkpoint
     */
    val arrival: ArrivalCheckpoint
        get() = sections[sections.size - 1].arrival

    val departureName: String
        get() = departure.stationName

    val arrivalName: String
        get() = arrival.location.name


    /**
     * Create a new connection from a parcel
     *
     * @param source the parcel
     */
    private constructor(source: Parcel): this(source.createTypedArrayList<Section>(Section.CREATOR))

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(sections)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Connection> {
        override fun createFromParcel(parcel: Parcel): Connection {
            return Connection(parcel)
        }

        override fun newArray(size: Int): Array<Connection?> {
            return arrayOfNulls(size)
        }
    }

}
