package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RealtimeInfoPart(val actualTime: Date,
                            val plattformChange: Boolean,
                            val undefinedDelay: Boolean,
                            val cancellation: Boolean): Parcelable


@Parcelize
data class RealtimeInfo(val arrival: RealtimeInfoPart,
                        val departure: RealtimeInfoPart): Parcelable
