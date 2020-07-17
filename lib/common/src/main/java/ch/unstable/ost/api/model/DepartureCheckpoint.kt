package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class DepartureCheckpoint(val departureTime: Date, val platform: String, val location: Location) : Parcelable