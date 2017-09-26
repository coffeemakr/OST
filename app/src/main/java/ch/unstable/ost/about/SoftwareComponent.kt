package ch.unstable.ost.about

import android.os.Parcel
import android.os.Parcelable

data class SoftwareComponent(val name: String,
                        val years: String,
                        val copyrightOwner: String,
                        val link: String,
                        val license: License) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeParcelable(license, flags)
        dest.writeString(copyrightOwner)
        dest.writeString(link)
        dest.writeString(years)
    }

    companion object {

        val CREATOR: Parcelable.Creator<SoftwareComponent> = object : Parcelable.Creator<SoftwareComponent> {
            override fun createFromParcel(source: Parcel): SoftwareComponent {
                val name = source.readString()
                val license:License = source.readParcelable(License::class.java.classLoader)
                val copyrightOwner = source.readString()
                val link = source.readString()
                val years = source.readString()
                return SoftwareComponent(name, years, copyrightOwner, link, license)
            }

            override fun newArray(size: Int): Array<SoftwareComponent?> {
                return arrayOfNulls(size)
            }
        }
    }
}
