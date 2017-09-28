package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.Objects

import java.util.Date

import ch.unstable.ost.utils.ParcelUtils

data class PassingCheckpoint(val arrivalTime: Date?, val departureTime: Date?, val stopLocation: StopLocation) : Parcelable {

    /**
     * Construct a new passing checkpoint
     *
     * if [arrivalTime] and [departureTime] is null it is considered to be a
     * checkpoint that doesn't stop
     *
     * TODO: 26.09.17  TBD: if arrivalTime or departureTime is null is boarding allowed?
     *
     * @param arrivalTime the arrival time
     * @param departureTime the departure time
     * @param location the location
     * @param platform the platform
     */
    constructor(arrivalTime: Date?, departureTime: Date?, location: Location, platform: String?) : this(arrivalTime, departureTime, StopLocation(platform, location))

    private constructor(source: Parcel) :this(
            departureTime = ParcelUtils.readDate(source),
            arrivalTime = ParcelUtils.readDate(source),
            stopLocation = ParcelUtils.readParcelable(source, StopLocation.CREATOR)!!)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        ParcelUtils.writeDate(dest, departureTime)
        ParcelUtils.writeDate(dest, arrivalTime)
        ParcelUtils.writeParcelable(dest, stopLocation, flags);
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<PassingCheckpoint> {
        override fun createFromParcel(`in`: Parcel): PassingCheckpoint {
            return PassingCheckpoint(`in`)
        }

        override fun newArray(size: Int): Array<PassingCheckpoint?> {
            return arrayOfNulls(size)
        }
    }

    val stationName: String
        get() = stopLocation.location.name

    val location: Location
        get() = stopLocation.location

    val platform: String?
        get() = stopLocation.platform
}
