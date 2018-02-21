package ch.unstable.sbb.api.model

import ch.unstable.ost.api.model.Location

data class Station(
        val displayName: String,
        val externalId: String?,
        val longitude: Long,
        val latitude: Long,
        val type: Location.StationType
)
