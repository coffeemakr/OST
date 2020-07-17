package ch.unstable.ost.views

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.CheckResult
import androidx.annotation.MainThread

/**
 * Animation strategy that animates the views using the alpha property.
 * TODO: fix (no views shown)
 */
class AlphaAnimationStrategy : AnimationStrategy {
    @MainThread
    override fun initHiddenView(view: View) {
        view.visibility = View.GONE
        view.alpha = 0f
    }

    @MainThread
    override fun initShownView(view: View) {
        view.alpha = 1f
        view.visibility = View.VISIBLE
    }

    @MainThread
    override fun showView(view: View) {
        val showAnimation = getShowAnimation(view)
        view.clearAnimation()
        view.startAnimation(showAnimation)
    }

    @MainThread
    override fun hideView(view: View) {
        val hideAnimation = getHideAnimation(view)
        view.clearAnimation()
        view.startAnimation(hideAnimation)
    }

    @MainThread
    override fun crossFadeViews(hideView: View, showView: View) {
        val hideAnimation = getHideAnimation(hideView)
        val showAnimation = getShowAnimation(showView)
        showView.clearAnimation()
        hideView.clearAnimation()
        showAnimation.startOffset = hideAnimation.duration
        showView.startAnimation(showAnimation)
        hideView.startAnimation(hideAnimation)
    }

    companion object {
        @CheckResult
        private fun getShowAnimation(view: View): Animation {
            val showAnimation: Animation
            if (view.visibility == View.VISIBLE) {
                showAnimation = AlphaAnimation(view.alpha, 1f)
            } else {
                view.alpha = 0f
                view.visibility = View.VISIBLE
                showAnimation = AlphaAnimation(0f, 1f)
            }
            return showAnimation
        }

        @CheckResult
        private fun getHideAnimation(view: View): Animation {
            val hideAnimation: Animation = AlphaAnimation(view.alpha, 0f)
            hideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            return hideAnimation
        }
    }
}