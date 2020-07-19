package ch.unstable.ost.test

import android.os.Parcel
import android.os.Parcelable
import ch.unstable.ost.api.model.*
import java.util.*

object TestHelper {
    @JvmStatic
    fun <T : Parcelable?> writeAndRead(parcelable: T, creator: Parcelable.Creator<T>): T {
        val parcel = Parcel.obtain()
        parcelable!!.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val result = creator.createFromParcel(parcel)
        parcel.recycle()
        return result
    }

    @JvmStatic
    fun generatePassingCheckpoints(number: Int): List<PassingCheckpoint> {
        val checkpoints = ArrayList<PassingCheckpoint>()
        for (i in 0 until number) {
            val arrival = Date(number.toLong())
            val departure = Date(number.toLong())
            val location = generateLocation(number)
            checkpoints.add(PassingCheckpoint(arrival, departure, location, "" + number))
        }
        return checkpoints
    }

    @JvmStatic
    fun generateRandomRoute(number: Int): Route {
        val shortName = "short $number"
        val longName = "long$number"
        val stops = generatePassingCheckpoints(25)
        return Route(shortName, longName, stops)
    }

    @JvmStatic
    fun generateDepartureCheckpoint(number: Int): DepartureCheckpoint {
        val departure = Date(number.toLong())
        val platform = number.toString()
        val location = generateLocation(number)
        return DepartureCheckpoint(departure, platform, location)
    }

    fun generateLocation(number: Int): Station {
        return Station("" + number, Station.StationType.TRAIN, "" + number)
    }

    @JvmStatic
    fun generateArrivalCheckpoint(number: Int): TimedCheckpoint {
        val arrival = Date(number.toLong())
        val platform = number.toString()
        val location = generateLocation(number)
        return TimedCheckpoint(arrival, platform, location, 0, 0)
    }
}