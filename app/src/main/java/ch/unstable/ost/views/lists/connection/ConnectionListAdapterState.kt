package ch.unstable.ost.views.lists.connection

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.api.model.Connection

data class ConnectionListAdapterState(
        val connections: Array<Connection>,
        val lowestPage: Int,
        val highestPage: Int
) : Parcelable {

    private constructor(parcel: Parcel): this(
            connections = parcel.createTypedArray(Connection),
            lowestPage = parcel.readInt(),
            highestPage = parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedArray(connections, flags)
        dest.writeInt(lowestPage)
        dest.writeInt(highestPage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<ConnectionListAdapterState> {
        override fun createFromParcel(parcel: Parcel): ConnectionListAdapterState {
            return ConnectionListAdapterState(parcel)
        }

        override fun newArray(size: Int): Array<ConnectionListAdapterState?> {
            return arrayOfNulls(size)
        }
    }
}