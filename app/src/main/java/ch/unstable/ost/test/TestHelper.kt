package ch.unstable.ost.test

import android.os.Parcel
import android.os.Parcelable

import java.util.Date

import ch.unstable.ost.api.model.ArrivalCheckpoint
import ch.unstable.ost.api.model.DepartureCheckpoint
import ch.unstable.ost.api.model.Location
import ch.unstable.ost.api.model.PassingCheckpoint
import ch.unstable.ost.api.model.Route

import java.util.Arrays.asList

fun generatePassingCheckpoints(number: Int): ArrayList<PassingCheckpoint> {
    val checkpoints: ArrayList<PassingCheckpoint> = ArrayList<PassingCheckpoint>()
    for (i in 0 until number) {
        val arrival = Date(number.toLong())
        val departure = Date(number.toLong())
        val location = generateLocation(number)
        val checkpoint = PassingCheckpoint(arrival, departure, location, "" + number)
        checkpoints.add(checkpoint)
    }
    return checkpoints
}

fun <T : Parcelable> writeAndRead(parcelable: T, creator: Parcelable.Creator<T>): T {
    val parcel = Parcel.obtain()
    parcelable.writeToParcel(parcel, 0)
    parcel.setDataPosition(0)
    val result = creator.createFromParcel(parcel)
    parcel.recycle()
    return result
}

fun generateRandomRoute(number: Int): Route {
    val shortName = "short " + number
    val longName = "long" + number
    val stops = generatePassingCheckpoints(25)
    return Route(shortName, longName, stops)
}

fun generateDepartureCheckpoint(number: Int): DepartureCheckpoint {
    val departure = Date(number.toLong())
    val platform = number.toString()
    val location = generateLocation(number)
    return DepartureCheckpoint(departure, platform, location)
}

fun generateLocation(number: Int): Location {
    return Location("" + number, "" + number, Location.StationType.TRAIN)
}

fun generateArrivalCheckpoint(number: Int): ArrivalCheckpoint {
    val arrival = Date(number.toLong())
    val platform = number.toString()
    val location = generateLocation(number)
    return ArrivalCheckpoint(arrival, platform, location)
}
