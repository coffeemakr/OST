package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportInfo(val direction: String, val label: String) : Parcelable