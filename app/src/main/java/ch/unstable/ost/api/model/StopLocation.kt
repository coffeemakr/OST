package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.utils.ParcelUtils

data class StopLocation(val platform: String?, val location: Location) : Parcelable {

    internal constructor(source: Parcel) : this(source.readString(), ParcelUtils.readParcelable(source, Location.CREATOR)!!);

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(platform)
        ParcelUtils.writeParcelable(dest, location, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StopLocation> {
        override fun createFromParcel(parcel: Parcel): StopLocation {
            return StopLocation(parcel)
        }

        override fun newArray(size: Int): Array<StopLocation?> {
            return arrayOfNulls(size)
        }
    }
}
