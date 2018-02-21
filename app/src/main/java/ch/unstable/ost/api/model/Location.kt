package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.Objects

import java.util.Arrays

import ch.unstable.ost.utils.ParcelUtils

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkNotNull


class Location : Parcelable {
    val name: String
    val type: StationType?
    private val id: String?

    constructor(name: String, type: StationType, id: String?) {

        checkNotNull(name, "name is null")
        checkArgument(name.isNotEmpty(), "name" + " must not be empty but was %s", name)
        this.name = name
        this.type = checkNotNull(type, "type")
        this.id = id
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        id = `in`.readString()
        type = ParcelUtils.readEnum(StationType.values(), `in`)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(id)
        ParcelUtils.writeEnum(dest, type)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getId(): String {
        return id ?: name
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val location = o as Location?
        return Objects.equal(name, location!!.name) &&
                type == location.type &&
                Objects.equal(id, location.id)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name, type, id)
    }

    enum class StationType private constructor(val bit: Int) {
        TRAIN(1),
        BUS(2),
        TRAM(4),
        SHIP(8),
        METRO(16),
        CABLEWAY(32),
        COG_RAILWAY(64),
        /// German: Standseilbahn
        FUNICULAR(128),
        ELEVATOR(256),
        POI(512),
        ADDRESS(1024),
        UNKNOWN(2048);


        companion object {

            fun getMask(vararg types: StationType): Int {
                var mask = 0
                for (type in types) {
                    mask = mask or type.bit
                }
                return mask
            }

            fun fromMask(mask: Int): Array<StationType> {
                val types = arrayOfNulls<StationType>(values().size)
                var length = 0
                for (type in values()) {
                    if (mask and type.bit > 0) {
                        types[length] = type
                        ++length
                    }
                }
                return Arrays.copyOf(types, length)
            }
        }
    }

    companion object {
        val CREATOR: Parcelable.Creator<Location> = object : Parcelable.Creator<Location> {
            override fun createFromParcel(`in`: Parcel): Location {
                return Location(`in`)
            }

            override fun newArray(size: Int): Array<Location?> {
                return arrayOfNulls(size)
            }
        }
    }
}
