package ch.unstable.ost.api.model

interface ConnectionPage {
    val pageNumber: Int
    val connections: List<Connection>
}