package ch.unstable.lib.sbb.model

data class Station(
        val displayName: String,
        val externalId: String?,
        val longitude: Long,
        val latitude: Long,
        val type: String
)
