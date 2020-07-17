package ch.unstable.ost.api

import ch.unstable.ost.api.model.ConnectionPage
import ch.unstable.ost.api.model.ConnectionQuery

interface ConnectionAPI {
    fun getConnections(connectionQuery: ConnectionQuery): ConnectionPage
}