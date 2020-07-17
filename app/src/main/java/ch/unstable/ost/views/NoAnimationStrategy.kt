package ch.unstable.ost.views

import android.view.View
import androidx.annotation.MainThread

/**
 * Animation strategy that doesn't animate anything.
 */
class NoAnimationStrategy : AnimationStrategy {
    @MainThread
    override fun initHiddenView(view: View) {
        view.visibility = View.GONE
    }

    @MainThread
    override fun initShownView(view: View) {
        view.visibility = View.VISIBLE
    }

    @MainThread
    override fun showView(view: View) {
        view.visibility = View.VISIBLE
    }

    @MainThread
    override fun hideView(view: View) {
        view.visibility = View.GONE
    }

    @MainThread
    override fun crossFadeViews(hideView: View, viewToShow: View) {
        hideView(hideView)
        showView(viewToShow)
    }
}