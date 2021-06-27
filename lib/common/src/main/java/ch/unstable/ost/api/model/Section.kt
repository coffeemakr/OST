package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * TODO: check if the stops contain the arrival and departure
 */
@Parcelize
class Section(
        val departure: TimedCheckpoint,
        val arrival: TimedCheckpoint,
        val type: SectionType,
        val transportInfo: TransportInfo?): Parcelable
