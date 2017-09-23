package ch.unstable.ost.views;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Animation strategy
 */
interface AnimationStrategy {
    @MainThread
    void initHiddenView(@NonNull final View view);

    @MainThread
    void initShownView(@NonNull final View view);

    @MainThread
    void showView(@NonNull final View view);

    @MainThread
    void hideView(@NonNull final View view);

    @MainThread
    void crossFadeViews(@NonNull final View hideView, @NonNull final View showView);
}
