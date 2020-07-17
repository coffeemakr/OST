package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
        val name: String,
        val type: StationType?,
        val id: String?
) : Parcelable {

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

            fun fromMask(mask: Int): Array<StationType> =
                    values().filter { type -> mask and type.bit > 0}.toTypedArray()
        }
    }
}
