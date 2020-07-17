package ch.unstable.ost

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.utils.ParcelUtils
import com.google.common.base.Objects
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.internal.toImmutableList
import java.util.*

@Parcelize
internal class SelectionState(
        private var via: List<String> = listOf(),
        private var from: String? = null,
        private var to: String? = null,
        private var departureTime: Date? = null,
        private var arrivalTime: Date? = null
) : Parcelable {

    @IgnoredOnParcel
    private val changeObservable = PublishSubject.create<SelectionState>()

    fun getChangeObservable(): Observable<SelectionState> {
        return changeObservable
    }

    private fun notifyChanged() {
        changeObservable.onNext(this)
    }

    fun setQuery(query: ConnectionQuery) {
        var changed: Boolean
        val vias: List<String> = query.via
        changed = vias != via
        via = vias
        changed = changed or !Objects.equal(from, query.from)
        from = query.from
        changed = changed or !Objects.equal(to, query.to)
        to = query.to
        changed = changed or !Objects.equal(departureTime, query.departureTime)
        departureTime = query.departureTime
        changed = changed or !Objects.equal(arrivalTime, query.arrivalTime)
        arrivalTime = query.arrivalTime
        if (changed) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Query changed: $query")
            notifyChanged()
        }
    }

    fun getVia(): List<String> {
        return via
    }

    fun setVia(via: List<String>) {
        this.via = via.toImmutableList()
        notifyChanged()
    }

    fun getFrom(): String? {
        return from
    }

    fun setFrom(from: String?) {
        if (!Objects.equal(this.from, from)) {
            this.from = from
            notifyChanged()
        }
    }

    fun getTo(): String? {
        return to
    }

    fun setTo(to: String?) {
        if (!Objects.equal(this.to, to)) {
            this.to = to
            notifyChanged()
        }
    }

    fun getDepartureTime(): Date? {
        return departureTime
    }

    fun setDepartureTime(departureTime: Date?) {
        this.departureTime = departureTime
        arrivalTime = null
        notifyChanged()
    }

    fun getArrivalTime(): Date? {
        return arrivalTime
    }

    fun setArrivalTime(arrivalTime: Date?) {
        departureTime = null
        this.arrivalTime = arrivalTime
        notifyChanged()
    }

    fun createQuery(): ConnectionQuery {
        return ConnectionQuery(
                from=from!!,
                to = to!!,
                arrivalTime = arrivalTime,
                departureTime = departureTime,
                via = via
        )
    }

    companion object {
        private const val TAG = "SelectionState"
        @JvmStatic
        fun createConnectionQuery(state: SelectionState) = state.createQuery()
    }
}