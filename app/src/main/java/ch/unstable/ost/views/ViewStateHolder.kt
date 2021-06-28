package ch.unstable.ost.views

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import ch.unstable.ost.BuildConfig
import ch.unstable.ost.R

class ViewStateHolder(private val mAnimationStrategy: AnimationStrategy) {
    private val handler: Handler

    private var errorMessage = 0
    private var errorView: View? = null
    private var errorTextView: TextView? = null
    private var errorRetryButton: View? = null
    private var onErrorRetryButtonClickListener: View.OnClickListener? = null
    private var loadingIndicator: View? = null
    private var contentView: View? = null
    private var state: State

    fun setOnRetryClickListener(listener: View.OnClickListener?) {
        onErrorRetryButtonClickListener = listener
        if (errorRetryButton != null) {
            errorRetryButton!!.setOnClickListener(onErrorRetryButtonClickListener)
        }
    }

    @MainThread
    fun setErrorContainer(errorContainer: View) {
        errorView = errorContainer
        errorTextView = errorContainer.findViewById(R.id.onErrorText)
        errorRetryButton = errorContainer.findViewById(R.id.onErrorRetryButton)
        errorRetryButton?.setOnClickListener(onErrorRetryButtonClickListener)
        if (state == State.FAILED) {
            mAnimationStrategy.initShownView(errorView!!)
            errorTextView?.setText(errorMessage)
        } else {
            mAnimationStrategy.initHiddenView(errorView!!)
        }
    }

    @MainThread
    fun setLoadingView(loadingView: View) {
        loadingIndicator = loadingView
        if (state == State.LOADING) {
            mAnimationStrategy.initShownView(loadingView)
        } else {
            mAnimationStrategy.initHiddenView(loadingView)
        }
    }

    @MainThread
    fun setContentView(contentView: View) {
        this.contentView = contentView
        if (state == State.SUCCEEDED) {
            mAnimationStrategy.initShownView(contentView)
        } else {
            mAnimationStrategy.initHiddenView(contentView)
        }
    }

    @AnyThread
    fun onError(@StringRes errorMessage: Int) {
        val message = handler.obtainMessage(MSG_ERROR)
        message.arg1 = errorMessage
        message.sendToTarget()
    }

    @AnyThread
    fun onSuccess() {
        handler.sendEmptyMessage(MSG_SUCCESS)
    }

    @AnyThread
    fun onLoading() {
        handler.sendEmptyMessage(MSG_LOAD)
    }

    @MainThread
    private fun onErrorSync(@StringRes errorMessage: Int) {
        this.errorMessage = errorMessage
        if (errorTextView != null) errorTextView!!.setText(errorMessage)
        switchToState(State.FAILED)
    }

    private val stateView: View?
        get() = getStateView(state)

    private fun getStateView(state: State): View? {
        return when (state) {
            State.FAILED -> errorView
            State.SUCCEEDED -> contentView
            State.LOADING -> loadingIndicator
            State.FRESH -> null
        }
    }

    @MainThread
    private fun onSuccessSync() {
        if (BuildConfig.DEBUG) Log.v(TAG, "onSuccessSync()")
        switchToState(State.SUCCEEDED)
    }

    private fun switchToState(newState: State) {
        if (state == newState) {
            Log.d(TAG, "State not changed: $newState")
            return
        }
        val currentView = stateView
        val nextView = getStateView(newState)
        if (currentView == null && nextView != null) {
            mAnimationStrategy.showView(nextView)
        } else if (currentView != null && nextView != null) {
            mAnimationStrategy.crossFadeViews(currentView, nextView)
        } else if (currentView != null) {
            mAnimationStrategy.hideView(currentView)
        }
        state = newState
    }

    @MainThread
    private fun onLoadingSync() {
        if (BuildConfig.DEBUG) Log.v(TAG, "onLoadingSync()")
        switchToState(State.LOADING)
    }

    private enum class State {
        FAILED, SUCCEEDED, LOADING, FRESH
    }

    private inner class Callback : Handler.Callback {
        override fun handleMessage(message: Message): Boolean {
            when (message.what) {
                MSG_ERROR -> {
                    onErrorSync(message.arg1)
                    return true
                }
                MSG_LOAD -> {
                    onLoadingSync()
                    return true
                }
                MSG_SUCCESS -> {
                    onSuccessSync()
                    return true
                }
            }
            return false
        }
    }

    companion object {
        private const val TAG = "ViewStateHolder"
        private const val MSG_ERROR = 1
        private const val MSG_SUCCESS = 2
        private const val MSG_LOAD = 3
    }

    init {
        handler = Handler(Looper.getMainLooper(), Callback())
        state = State.FRESH
    }
}