package ch.unstable.ost.api.model

import android.os.Parcelable

interface ConnectionPage: Parcelable {
    val pageNumber: Int
    val connections: List<Connection>
}