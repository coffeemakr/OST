package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportInfo(
        val direction: String,
        val label: String,
        val icon: String,
        val iconSuffix: String?,
        val text: String,
        val name: String?
) : Parcelable {
    val displayName: String
        get() {
            if (iconSuffix != null) {
                return "$icon $iconSuffix"
            } else {
                return "$icon $name"
            }
        }
}