package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.MoreObjects
import com.google.common.base.Objects

import java.util.Date

import ch.unstable.ost.utils.ParcelUtils

data class ArrivalCheckpoint(val arrivalTime: Date, val stopLocation: StopLocation) : Parcelable {

    constructor(arrivalTime: Date, platform: String, location: Location) : this(arrivalTime, StopLocation(platform, location))

    private constructor(source: Parcel) : this(
            arrivalTime = ParcelUtils.readDate(source)!!,
            stopLocation = ParcelUtils.readParcelable(source, StopLocation.CREATOR)!!)

    val location: Location
        get() = stopLocation.location

    val platform: String?
        get() = stopLocation.platform

    override fun writeToParcel(dest: Parcel, flags: Int) {
        ParcelUtils.writeDate(dest, arrivalTime)
        ParcelUtils.writeParcelable(dest, stopLocation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArrivalCheckpoint> {
        override fun createFromParcel(parcel: Parcel): ArrivalCheckpoint {
            return ArrivalCheckpoint(parcel)
        }

        override fun newArray(size: Int): Array<ArrivalCheckpoint?> {
            return arrayOfNulls(size)
        }
    }

}
