package ch.unstable.ost.utils

import android.os.Parcel
import android.os.ParcelFormatException
import android.os.Parcelable
import ch.unstable.ost.utils.ParcelCompat.readTypeObject
import ch.unstable.ost.utils.ParcelCompat.writeTypeObject
import com.google.common.base.Preconditions
import java.util.*

object ParcelUtils {
    @JvmStatic
    fun readDate(`in`: Parcel): Date? {
        val timestamp = `in`.readLong()
        return if (timestamp == -1L) {
            null
        } else {
            Date(timestamp)
        }
    }

    @JvmStatic
    fun writeDate(out: Parcel, date: Date?) {
        if (date == null) {
            out.writeLong(-1)
        } else {
            out.writeLong(date.time)
        }
    }

    @JvmStatic
    fun writeNullableInteger(out: Parcel, integer: Int?) {
        if (integer == null) {
            out.writeInt(Int.MIN_VALUE)
        } else {
            out.writeInt(integer)
        }
    }

    @JvmStatic
    fun readNullableInteger(`in`: Parcel): Int? {
        val value = `in`.readInt()
        return if (value == Int.MIN_VALUE) {
            null
        } else {
            value
        }
    }

    @JvmStatic
    fun <E> readEnum(values: Array<E>, `in`: Parcel): E? {
        val ordinal = `in`.readInt()
        return if (ordinal == -1) {
            null
        } else {
            values[ordinal]
        }
    }

    @JvmStatic
    fun writeEnum(dest: Parcel, enumeration: Enum<*>?) {
        if (enumeration == null) {
            dest.writeInt(-1)
        } else {
            dest.writeInt(enumeration.ordinal)
        }
    }

    fun <T : Parcelable?> readParcelable(`in`: Parcel?, creator: Parcelable.Creator<T>?): T? {
        return readTypeObject(`in`!!, creator!!)
    }

    fun writeParcelable(dest: Parcel?, parcelable: Parcelable?, flags: Int) {
        writeTypeObject(dest!!, parcelable, flags)
    }

    @JvmStatic
    fun writeNullableLong(dest: Parcel, value: Long?) {
        if (value != null) {
            dest.writeLong(value)
        } else {
            dest.writeLong(Long.MIN_VALUE)
        }
    }

    @JvmStatic
    fun readNullableLong(`in`: Parcel): Long? {
        val value = `in`.readLong()
        return if (value == Long.MIN_VALUE) {
            null
        } else {
            value
        }
    }

    @JvmStatic
    fun <T : Parcelable?> readNonNulTypedObject(`in`: Parcel?, creator: Parcelable.Creator<T>?): T {
        return readTypeObject(`in`!!, creator!!)
                ?: throw ParcelFormatException("Read value can't be null")
    }

    @JvmStatic
    fun <T : Parcelable?> writeNonNullTypedObject(dest: Parcel?, value: T, flags: Int) {
        Preconditions.checkNotNull(value, "value is null")
        writeTypeObject(dest!!, value, flags)
    }
}