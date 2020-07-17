package ch.unstable.ost.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Construct a new passing checkpoint
 *
 * if \param arrivalTime and \param departureTime is null it is considered to be a
 * checkpoint that doesn't stop
 *
 * TODO: 26.09.17  TBD: if arrivalTime or departureTime is null is boarding allowed?
 *
 * @param arrivalTime the arrival time
 * @param departureTime the departure time
 * @param location the location
 * @param platform the platform
 */
@Parcelize
class PassingCheckpoint(val arrivalTime: Date, val departureTime: Date, val location: Location, val platform: String): Parcelable