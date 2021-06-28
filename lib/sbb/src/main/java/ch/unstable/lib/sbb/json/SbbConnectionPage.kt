package ch.unstable.lib.sbb.model

import kotlinx.serialization.Serializable

@Serializable
data class LegendItem(
        val code: String,
        val description: String,
)

@Serializable
data class LegendOccupancyItem(
        val code: String,
        val description: String,
)

@Serializable
data class SbbConnectionRealtimeInfo(
        val abfahrtIstDatum: String,
        val abfahrtIstZeit: String,
        val alternativeMsg: String?,
        val ankunftIstDatum: String,
        val ankunftIstZeit: String,
        val cancellationMsg: String?,
        val detailMsg: String?,
        val icon: String?,
        val isAlternative: Boolean,
        val nextAlternative: String?,
        val platformChange: Boolean?
)


//"abfahrtIstZeit": "13:35",
//"abfahrtIstDatum": "28.06.2021",
//"ankunftIstZeit": "14:26",
//"ankunftIstDatum": "28.06.2021",
//"abfahrtDelay": "ca. +2'",
//"abfahrtDelayAccessibility": "mit Verspätung erwartet um ca. +2'",
//"realtimeMeldungen": [
//{
//    "type": "sbb_realtime_delay",
//    "summaryText": "Verspätung",
//    "summaryTextPlain": "Verspätung",
//    "title": null,
//    "titlePlain": null,
//    "text": null,
//    "textPlain": null,
//    "descriptionAccessibility": "Verspätung",
//    "sortOrder": 0,
//    "urls": [],
//    "displayIcon": true
//}
//],
//"ankunftPlatformChange": false,
//"abfahrtCancellation": false,
//"abfahrtDelayUndefined": false,
//"abfahrtPlatformChange": false,
//"ankunftCancellation": false,
//"ankunftDelayUndefined": false


//"abfahrtIstZeit": "13:43",
//"abfahrtIstDatum": "28.06.2021",
//"ankunftIstZeit": "14:52",
//"ankunftIstDatum": "28.06.2021",
//"ankunftPlatformChange": false,
//"abfahrtCancellation": false,
//"abfahrtDelayUndefined": false,
//"abfahrtPlatformChange": false,
//"ankunftCancellation": false,
//"ankunftDelayUndefined": false


@Serializable
data class SbbSectionRealtimeMeldungen(
        val type: String,
        val summaryText: String?,
        val summaryTextPlain: String?,
        val title: String?,
        val titlePlain: String?,
        val text: String?,
        val textPlain: String?,
        val descriptionAccessibility: String,
        val sortOrder: Int,
        val urls: List<String>,
        val displayIcon: Boolean,
)

@Serializable
data class SbbSectionRealtimeInfo(
        val abfahrtCancellation: Boolean,
        val abfahrtDelay: String? = null,
        val abfahrtDelayAccessibility: String? = null,
        val abfahrtIstDatum: String,
        val abfahrtIstZeit: String,
        val abfahrtPlatformChange: Boolean,
        val abfahrtDelayUndefined: Boolean,
        val ankunftCancellation: Boolean,
        val ankunftDelayUndefined: Boolean,
        val ankunftDelay: String? = null,
        val ankunftDelayAccessibility: String? = null,
        val ankunftIstDatum: String,
        val ankunftIstZeit: String,
        val ankunftPlatformChange: Boolean,
        val cancellationMsg: String? = null,
        val cancellationMsgAccessibility: String? = null,
        val realtimeMeldungen: List<SbbSectionRealtimeMeldungen>? = emptyList()
)

@Serializable
data class SbbCoordinates(
        val latitude: Long,
        val longitude: Long,
)

@Serializable
data class SbbSection(
        val abfahrtBarriereFreiheit: String? = null,
        val abfahrtCancellation: Boolean,
        val abfahrtDatum: String,
        val abfahrtDelay: String? = null,
        val abfahrtDelayAccessibility: String? = null,
        val abfahrtGleis: String? = null,
        val abfahrtKoordinaten: SbbCoordinates,
        val abfahrtName: String,
        val abfahrtPlatformChange: Boolean,
        val abfahrtTime: String,
        val abfahrtZugformation: String? = null,
        val actionUrl: String,
        val ankunftBarriereFreiheit: String? = null,
        val ankunftCancellation: Boolean,
        val ankunftDatum: String,
        val ankunftDelay: String? = null,
        val ankunftDelayAccessibility: String? = null,
        val ankunftGleis: String? = null,
        val ankunftKoordinaten: SbbCoordinates,
        val ankunftName: String,
        val ankunftPlatformChange: Boolean,
        val ankunftTime: String,
        val ankunftZugformation: String? = null,
        val belegungErste: String,
        val belegungZweite: String,
        val cancellationMsg: String? = null,
        val cancellationMsgAccessibility: String? = null,
        val distance: String? = null,
        val duration: String? = null,
        val durationProzent: String? = null,
        val realtimeInfo: SbbSectionRealtimeInfo? = null,
        //val realtimeInfos: String?,
        val transportBezeichnung: SbbTransportDescription? = null,
        val transportHinweis: String? = null,
        val transportServiceAttributes: List<String>,
        val type: String,
        val walkBezeichnung: String? = null,
        val walkBezeichnungAccessibility: String? = null,
)

@Serializable
data class SbbConnection(
        val abfahrt: String,
        val abfahrtDate: String,
        val abfahrtGleis: String? = null,
        val abfahrtTime: String,
        val angeboteUrl: String,
        val ankunft: String,
        val ankunftDate: String,
        val ankunftGleis: String? = null,
        val ankunftTime: String,
        val belegungErste: String,
        val belegungZweite: String,
        val dayDifference: String,
        val dayDifferenceAccessibility: String,
        val duration: String,
        val durationAccessibility: String,
        val isInternationalVerbindung: Boolean = false,
        //val legendBfrItems: List<String>,
        val legendItems: List<LegendItem> = emptyList(),
        val legendOccupancyItems: List<LegendOccupancyItem> = emptyList(),
        val realtimeInfo: SbbConnectionRealtimeInfo,
        val reconstructionContext: String,
        val serviceAttributes: List<String>,
        val transfers: Int,
        val transportBezeichnung: SbbTransportDescription,
        val verbindungAbpreisContext: String,
        val verbindungId: String,
        val verbindungSections: List<SbbSection>,
        //val verkehrstage: List<>
        val vias: List<String>? = null,
        val zuschlagspflicht: Boolean
)

@Serializable
data class SbbTransportDescription(
        val oevIcon: String,
        val transportDirection: String,
        val transportIcon: String,
        val transportIconSuffix: String?,
        val transportLabel: String,
        val transportLabelBgColor: String?,
        val transportLabelTextColor: String?,
        val transportName: String?,
        val transportText: String
)

@Serializable
data class SbbStation(val barriereFreiheit: String,
                      val displayName: String,
                      val externalId: String,
                      val latitude: Long,
                      val longitude: Long,
                      val type: String)

@Serializable
data class SbbConnectionPage(
        val abfahrt: SbbStation,
        val ankunft: SbbStation,
        val earlierUrl: String,
        val laterUrl: String,
        val legendBfrItems: List<String>,
        val legendItems: List<LegendItem>,
        val legendOccupancyItems: List<LegendOccupancyItem>,
        val verbindungPreisUrl: String,
        val verbindungen: List<SbbConnection>)