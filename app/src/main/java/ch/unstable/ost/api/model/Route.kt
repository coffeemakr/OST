package ch.unstable.ost.api.model


import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.Objects

import java.util.Arrays

import com.google.common.base.Preconditions.checkNotNull

data class Route(val shortName: String, val longName: String, val stops: List<PassingCheckpoint>) : Parcelable {

    constructor(`in`: Parcel): this(
            shortName = `in`.readString(),
            longName = `in`.readString(),
            stops = `in`.createTypedArrayList(PassingCheckpoint))

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(shortName)
        dest.writeString(longName)
        dest.writeTypedList(stops)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Route> {
        override fun createFromParcel(`in`: Parcel): Route {
            return Route(`in`)
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}
