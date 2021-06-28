package ch.unstable.ost

import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import ch.unstable.ost.api.model.ConnectionQuery

abstract class BaseNavigationFragment : Fragment() {
    private var listener: OnRouteSelectionListener? = null
    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is OnRouteSelectionListener) {
            context
        } else {
            throw IllegalStateException(context.toString()
                    + " must implement OnRouteSelectionListener")
        }
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun selectRoute(query: ConnectionQuery) {
        listener?.onRouteSelected(query)
    }

    interface OnRouteSelectionListener {
        @MainThread
        fun onRouteSelected(query: ConnectionQuery)
    }
}