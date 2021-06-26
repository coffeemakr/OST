package ch.unstable.ost.utils

import android.os.Build
import android.os.Parcel
import android.os.ParcelFormatException
import android.os.Parcelable

object ParcelCompat {

    private fun <T : Parcelable?> readTypeObjectCompat(`in`: Parcel, creator: Parcelable.Creator<T>): T? {
        val exists = `in`.readInt()
        return if (exists == 1) {
            creator.createFromParcel(`in`)
        } else if (exists == 0) {
            null
        } else {
            throw ParcelFormatException("Expected exists to be 0 or 1!")
        }
    }

    @JvmStatic
    fun <T : Parcelable?> readTypeObject(`in`: Parcel, creator: Parcelable.Creator<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            `in`.readTypedObject(creator)
        } else {
            readTypeObjectCompat(`in`, creator)
        }
    }

    private fun <T : Parcelable?> writeTypeObjectCompat(dest: Parcel, `val`: T?, flags: Int) {
        if (`val` != null) {
            dest.writeInt(1)
            `val`.writeToParcel(dest, flags)
        } else {
            dest.writeInt(0)
        }
    }

    @JvmStatic
    fun <T : Parcelable?> writeTypeObject(dest: Parcel, parcelable: T?, flags: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dest.writeTypedObject(parcelable, flags)
        } else {
            writeTypeObjectCompat(dest, parcelable, flags)
        }
    }
}