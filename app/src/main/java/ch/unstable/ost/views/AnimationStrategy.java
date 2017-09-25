package ch.unstable.ost.views;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Animation strategy
 */
interface AnimationStrategy {
    /**
     * Initialize a view in a hidden state
     * @param view the view to initialize
     */
    @MainThread
    void initHiddenView(@NonNull final View view);

    /**
     * Initialize a view in a shown state
     * @param view the view to initialize
     */
    @MainThread
    void initShownView(@NonNull final View view);

    /**
     * Animate showing a view
     * @param view the view
     */
    @MainThread
    void showView(@NonNull final View view);

    /**
     * Animate hiding a view
     * @param view the view to hide
     */
    @MainThread
    void hideView(@NonNull final View view);

    /**
     * Animate hiding a view and simultaneously showing another
     * @param hideView the view to hide
     * @param showView the view to show
     */
    @MainThread
    void crossFadeViews(@NonNull final View hideView, @NonNull final View showView);
}
