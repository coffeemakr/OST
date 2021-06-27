package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportInfo(
        val direction: String,
        val label: String?,
        val icon: String,
        val iconSuffix: String?,
        val text: String,
        val name: String?
) : Parcelable {
    val shortDisplayName: String
        get() {
            return if (label != null) {
                "$label"
            } else if (iconSuffix != null) {
                "$icon $iconSuffix"
            } else {
                "$icon $name"
            }
        }
}