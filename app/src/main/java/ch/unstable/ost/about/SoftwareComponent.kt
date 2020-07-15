package ch.unstable.ost.about

import android.os.Parcel
import android.os.Parcelable

data class SoftwareComponent(
        val name: String,
        val years: String,
        val copyrightOwner: String,
        val link: String,
        val license: License,
        val version: String? = null
) : Parcelable {

    private constructor(`in`: Parcel) : this(
            name = `in`.readString()!!,
            license = `in`.readParcelable(License::class.java.classLoader)!!,
            copyrightOwner = `in`.readString()!!,
            link = `in`.readString()!!,
            years = `in`.readString()!!,
            version = `in`.readString())


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeParcelable(license, flags)
        dest.writeString(copyrightOwner)
        dest.writeString(link)
        dest.writeString(years)
        dest.writeString(version)
    }

    companion object {
        val CREATOR: Parcelable.Creator<SoftwareComponent> = object : Parcelable.Creator<SoftwareComponent> {
            override fun createFromParcel(source: Parcel): SoftwareComponent {
                return SoftwareComponent(source)
            }

            override fun newArray(size: Int): Array<SoftwareComponent?> {
                return arrayOfNulls(size)
            }
        }
    }
}