package ch.unstable.lib.sbb.model

import kotlinx.serialization.Serializable

@Serializable
data class SbbStationResponse(
        val barriereFreiheit: String?,
        val displayName: String,
        val externalId: String,
        val latitude: Long,
        val longitude: Long,
        val type: String,
)

@Serializable
data class SbbStationsResponse(val standorte: List<SbbStationResponse>)