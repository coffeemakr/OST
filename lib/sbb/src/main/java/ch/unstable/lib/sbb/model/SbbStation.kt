package ch.unstable.lib.sbb.model

data class SbbStation(
        val displayName: String,
        val externalId: String?,
        val longitude: Long,
        val latitude: Long,
        val type: String
)
