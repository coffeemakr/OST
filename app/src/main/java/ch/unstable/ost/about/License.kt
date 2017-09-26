package ch.unstable.ost.about

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * A software license
 */
class License(val abbreviation: String, val name: String, val filename: String) : Parcelable {

    val contentUri: Uri
        get() = Uri.Builder()
                .scheme("file")
                .path("/android_asset")
                .appendPath(filename)
                .build()

    private constructor(input: Parcel): this(input.readString(), input.readString(), input.readString());

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.filename)
        dest.writeString(this.abbreviation)
        dest.writeString(this.name)
    }

    companion object {

        val CREATOR: Parcelable.Creator<License> = object : Parcelable.Creator<License> {
            override fun createFromParcel(source: Parcel): License {
                return License(source)
            }

            override fun newArray(size: Int): Array<License?> {
                return arrayOfNulls(size)
            }
        }
    }
}
