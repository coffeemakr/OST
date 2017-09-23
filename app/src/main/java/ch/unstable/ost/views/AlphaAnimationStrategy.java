package ch.unstable.ost.views;

import android.support.annotation.CheckResult;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Animation strategy that animates the views using the alpha property.
 * TODO: fix (no views shown)
 */
public class AlphaAnimationStrategy implements AnimationStrategy {
    @CheckResult
    private static Animation getShowAnimation(@NonNull final View view) {
        Animation showAnimation;
        if (view.getVisibility() == View.VISIBLE) {
            showAnimation = new AlphaAnimation(view.getAlpha(), 1f);
        } else {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            showAnimation = new AlphaAnimation(0f, 1f);
        }
        return showAnimation;
    }

    @CheckResult
    private static Animation getHideAnimation(@NonNull final View view) {
        Animation hideAnimation = new AlphaAnimation(view.getAlpha(), 0f);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return hideAnimation;
    }

    @MainThread
    public void initHiddenView(@NonNull View view) {
        view.setVisibility(View.GONE);
        view.setAlpha(0f);
    }

    @MainThread
    public void initShownView(@NonNull View view) {
        view.setAlpha(1f);
        view.setVisibility(View.VISIBLE);
    }

    @MainThread
    @Override
    public void showView(@NonNull View view) {
        Animation showAnimation = getShowAnimation(view);
        view.clearAnimation();
        view.startAnimation(showAnimation);
    }

    @MainThread
    @Override
    public void hideView(@NonNull View view) {
        Animation hideAnimation = getHideAnimation(view);
        view.clearAnimation();
        view.startAnimation(hideAnimation);
    }

    @MainThread
    @Override
    public void crossFadeViews(@NonNull View hideView, @NonNull View showView) {
        Animation hideAnimation = getHideAnimation(hideView);
        Animation showAnimation = getShowAnimation(showView);
        showView.clearAnimation();
        hideView.clearAnimation();
        showAnimation.setStartOffset(hideAnimation.getDuration());
        showView.startAnimation(showAnimation);
        hideView.startAnimation(hideAnimation);
    }
}
