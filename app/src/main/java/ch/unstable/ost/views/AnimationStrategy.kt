package ch.unstable.ost.views

import android.view.View
import androidx.annotation.MainThread

/**
 * Animation strategy
 */
internal interface AnimationStrategy {
    /**
     * Initialize a view in a hidden state
     * @param view the view to initialize
     */
    @MainThread
    fun initHiddenView(view: View)

    /**
     * Initialize a view in a shown state
     * @param view the view to initialize
     */
    @MainThread
    fun initShownView(view: View)

    /**
     * Animate showing a view
     * @param view the view
     */
    @MainThread
    fun showView(view: View)

    /**
     * Animate hiding a view
     * @param view the view to hide
     */
    @MainThread
    fun hideView(view: View)

    /**
     * Animate hiding a view and simultaneously showing another
     * @param hideView the view to hide
     * @param showView the view to show
     */
    @MainThread
    fun crossFadeViews(hideView: View, showView: View)
}