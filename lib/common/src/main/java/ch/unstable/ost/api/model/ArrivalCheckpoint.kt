package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class TimedCheckpoint(
        val time: Date,
        val platform: String,
        val station: Station,
        val longitude: Long,
        val latitude: Long
): Parcelable