package ch.unstable.lib.sbb.model

import ch.unstable.lib.sbb.json.readDateTime
import ch.unstable.ost.api.model.*
import kotlinx.android.parcel.Parcelize


fun getArrival(sbbSection: SbbSection): TimedCheckpoint {
    return TimedCheckpoint(
            time = readDateTime(
                    date = sbbSection.ankunftDatum,
                    time = sbbSection.ankunftTime,
            ),
            platform = sbbSection.ankunftGleis.orEmpty(),
            latitude = sbbSection.ankunftKoordinaten.latitude,
            longitude = sbbSection.ankunftKoordinaten.longitude,
            station = Station(
                    name = sbbSection.ankunftName,
                    type = Station.StationType.UNKNOWN,
                    id = null
            )
    )
}

fun getDeparture(sbbSection: SbbSection): TimedCheckpoint {
    return TimedCheckpoint(
            time = readDateTime(
                    date = sbbSection.abfahrtDatum,
                    time = sbbSection.abfahrtTime,
            ),
            platform = sbbSection.abfahrtGleis.orEmpty(),
            latitude = sbbSection.abfahrtKoordinaten.latitude,
            longitude = sbbSection.abfahrtKoordinaten.longitude,
            station = Station(
                    name = sbbSection.abfahrtName,
                    type = Station.StationType.UNKNOWN,
                    id = null
            )
    )
}

fun convertSection(sbbSection: SbbSection): Section {
    val type: SectionType?
    val transportInfo: TransportInfo?
    val realtimeInfo: RealtimeInfo?
    when (sbbSection.type) {
        "WALK" -> {
            type = SectionType.WALK
            transportInfo = null
            realtimeInfo = null
        }
        "TRANSPORT" -> {
            type = SectionType.TRANSPORT
            transportInfo = getTransportInfo(sbbSection.transportBezeichnung!!)
            realtimeInfo = sbbSection.realtimeInfo?.let { getRealtimeInfo(it) }

        }
        else -> error("Unknown type")
    }


    return Section(
            departure = getDeparture(sbbSection),
            arrival = getArrival(sbbSection),
            type = type,
            transportInfo = transportInfo,
            realtimeInfo = realtimeInfo,
    )
}

fun getRealtimeInfo(realtimeInfo: SbbSectionRealtimeInfo): RealtimeInfo {
    return RealtimeInfo(
            departure = RealtimeInfoPart(
                    actualTime = readDateTime(
                            date = realtimeInfo.abfahrtIstDatum,
                            time = realtimeInfo.abfahrtIstZeit,
                    ),
                    plattformChange = realtimeInfo.abfahrtPlatformChange,
                    undefinedDelay = realtimeInfo.abfahrtDelayUndefined,
                    cancellation = realtimeInfo.abfahrtCancellation,
            ),
            arrival = RealtimeInfoPart(
                    actualTime = readDateTime(
                            date = realtimeInfo.ankunftIstDatum,
                            time = realtimeInfo.ankunftIstZeit,
                    ),
                    plattformChange = realtimeInfo.ankunftPlatformChange,
                    undefinedDelay = realtimeInfo.ankunftDelayUndefined,
                    cancellation = realtimeInfo.ankunftCancellation,
            )
    )
}

fun getTransportInfo(transportBezeichnung: SbbTransportDescription): TransportInfo {
    return TransportInfo(
            direction = transportBezeichnung.transportDirection,
            label = transportBezeichnung.transportLabel,
            icon = transportBezeichnung.transportIcon,
            iconSuffix = transportBezeichnung.transportIconSuffix,
            text = transportBezeichnung.transportText,
            name = transportBezeichnung.transportName,
    )
}

@Parcelize
data class SbbConnectionWrapper(
        override val sections: List<Section>
) : Connection {
    companion object {
        fun fromRaw(sbbConnection: SbbConnection): SbbConnectionWrapper {
            return SbbConnectionWrapper(
                    sections = sbbConnection.verbindungSections.map { convertSection(it) },
            )
        }
    }
}

@Parcelize
data class SbbConnectionPageWrapper(
        val earlierUrl: String,
        val laterUrl: String,
        override val pageNumber: Int,
        override val connections: List<Connection>
) : ConnectionPage {
    companion object {
        fun fromRaw(pageNumber: Int, connectionPage: SbbConnectionPage): SbbConnectionPageWrapper {
            val connections = connectionPage.verbindungen.map { sbbConnection ->
                SbbConnectionWrapper.fromRaw(sbbConnection)
            }
            return SbbConnectionPageWrapper(
                    connections = connections,
                    pageNumber = pageNumber,
                    earlierUrl = connectionPage.earlierUrl,
                    laterUrl = connectionPage.laterUrl,
            )
        }
    }
}