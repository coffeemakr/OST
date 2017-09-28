package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.utils.ParcelUtils
import java.util.*

data class DepartureCheckpoint(val departureTime: Date, val stopLocation: StopLocation) : Parcelable {

    constructor(departureTime: Date, platform: String?, location: Location):
            this(departureTime, StopLocation(platform, location));

    private constructor(source: Parcel): this(
            departureTime = ParcelUtils.readDate(source)!!,
            stopLocation = ParcelUtils.readParcelable(source, StopLocation.CREATOR)!!)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        ParcelUtils.writeDate(dest, departureTime)
        ParcelUtils.writeParcelable(dest, stopLocation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR: Parcelable.Creator<DepartureCheckpoint> {
        override fun createFromParcel(`in`: Parcel): DepartureCheckpoint {
            return DepartureCheckpoint(`in`)
        }

        override fun newArray(size: Int): Array<DepartureCheckpoint?> {
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
