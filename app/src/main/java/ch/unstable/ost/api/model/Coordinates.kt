package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable

data class Coordinates(val x: Double, val y: Double) : Parcelable {

    private constructor(source: Parcel): this(source.readDouble(), source.readDouble())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(x)
        dest.writeDouble(y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Coordinates> {
        override fun createFromParcel(source: Parcel): Coordinates {
            return Coordinates(source)
        }

        override fun newArray(size: Int): Array<Coordinates?> {
            return arrayOfNulls(size)
        }
    }
}
