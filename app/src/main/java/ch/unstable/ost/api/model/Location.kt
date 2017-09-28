package ch.unstable.ost.api.model

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.utils.ParcelUtils
import java.util.*


data class Location(val id: String?, val name: String, val type: StationType) : Parcelable {

    private constructor(source: Parcel): this(
            id = source.readString(),
            name = source.readString(),
            type = ParcelUtils.readEnum(StationType.values(), source))

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        ParcelUtils.writeEnum(dest, type)
    }

    override fun describeContents(): Int {
        return 0
    }


    enum class StationType(val bit: Int) {
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

    companion object CREATOR: Parcelable.Creator<Location> {
        override fun createFromParcel(`in`: Parcel): Location {
            return Location(`in`)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}
