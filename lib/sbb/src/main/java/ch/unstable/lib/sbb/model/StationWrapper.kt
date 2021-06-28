package ch.unstable.lib.sbb.model

import android.util.Log
import ch.unstable.ost.api.model.Coordinates
import ch.unstable.ost.api.model.Station

private fun convertToType(stringType: String): Station.StationType {
    return when (stringType) {
        "STATION" -> Station.StationType.TRAIN
        "POI" -> Station.StationType.POI
        "ADDRESS" -> Station.StationType.ADDRESS
        else -> {
            Log.w("convertToType", "Unknown type: $stringType")
            Station.StationType.UNKNOWN
        }
    }
}

fun convertStation(station: SbbStationResponse): Station {
    return Station(
            name = station.displayName,
            type = convertToType(station.type),
            id = station.externalId,
            coordinates = Coordinates(
                    x = station.latitude.div(1000000.0),
                    y = station.longitude.div(1000000.0),
            )
    )
}

