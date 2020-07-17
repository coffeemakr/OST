package ch.unstable.lib.sbb.model

import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.ConnectionPage

data class SbbConnectionPage(
        val start: SbbStation,
        val destination: SbbStation,
        val earlierUrl: String,
        val laterUrl: String,
        override val connections: List<Connection>,
        override val pageNumber: Int = 0): ConnectionPage