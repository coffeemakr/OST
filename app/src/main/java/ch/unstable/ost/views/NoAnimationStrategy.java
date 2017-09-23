package ch.unstable.ost.views;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Animation strategy that doesn't animate anything.
 */
public class NoAnimationStrategy implements AnimationStrategy {

    @MainThread
    @Override
    public void initHiddenView(@NonNull View view) {
        view.setVisibility(View.GONE);
    }

    @MainThread
    @Override
    public void initShownView(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
    }

    @MainThread
    @Override
    public void showView(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
    }

    @MainThread
    @Override
    public void hideView(@NonNull View view) {
        view.setVisibility(View.GONE);
    }

    @MainThread
    @Override
    public void crossFadeViews(@NonNull View viewToHide, @NonNull View viewToShow) {
        hideView(viewToHide);
        showView(viewToShow);
    }
}
