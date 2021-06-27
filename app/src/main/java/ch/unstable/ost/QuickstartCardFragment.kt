package ch.unstable.ost

import android.view.View
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.room.EmptyResultSetException
import ch.unstable.ost.error.ErrorUtils.showErrorSnackbar
import io.reactivex.functions.Consumer

abstract class QuickstartCardFragment : Fragment() {
    protected val errorConsumer: Consumer<Throwable>
        get() = Consumer { throwable: Throwable ->
            if (throwable is EmptyResultSetException) {
                onNoElement()
            } else {
                onError(throwable)
            }
        }

    private fun onNoElement() {
        hideCard()
    }

    @MainThread
    private fun onError(throwable: Throwable) {
        hideCard()
        showErrorSnackbar(requireView(), errorMessage, throwable)
    }

    @get:StringRes
    protected abstract val errorMessage: Int

    private fun hideCard() {
        val view = view
        if (view != null) {
            // TODO: animate
            view.visibility = View.GONE
        }
    }
}