package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Route(val shortName: String, val longName: String, val stops: List<PassingCheckpoint>) : Parcelable