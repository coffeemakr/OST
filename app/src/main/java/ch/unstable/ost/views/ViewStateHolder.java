package ch.unstable.ost.views;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewStateHolder {

    private static final String TAG = "ViewStateHolder";
    private final AnimationStrategy mAnimationStrategy;
    private final Handler handler;
    private final int MSG_ERROR = 1;
    private final int MSG_SUCCESS = 2;
    private final int MSG_LOAD = 3;
    private int errorMessage = 0;
    private View mErrorView;
    private TextView mErrorTextView;
    private View mErrorRetryButton;
    @Nullable
    private View.OnClickListener mOnErrorRetryButtonClickListener;
    private View mLoadingIndicator;
    private View mContentView;
    private State state;

    public ViewStateHolder(AnimationStrategy animationStrategy) {
        this.mAnimationStrategy = Preconditions.checkNotNull(animationStrategy, "animationStrategy");
        this.handler = new Handler(Looper.getMainLooper(), new Callback());
        this.state = State.FRESH;
    }

    public void setOnRetryClickListener(View.OnClickListener listener) {
        mOnErrorRetryButtonClickListener = listener;
        if (mErrorRetryButton != null) {
            mErrorRetryButton.setOnClickListener(mOnErrorRetryButtonClickListener);
        }
    }

    @MainThread
    public void setErrorContainer(View errorContainer) {
        this.mErrorView = checkNotNull(errorContainer, "errorContainer is null");
        mErrorTextView = errorContainer.findViewById(R.id.onErrorText);
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(mErrorTextView, "onErrorText is null");
        mErrorRetryButton = errorContainer.findViewById(R.id.onErrorRetryButton);
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(mErrorRetryButton, "onErrorRetryButton is null");
        mErrorRetryButton.setOnClickListener(mOnErrorRetryButtonClickListener);
        if (state == State.FAILED) {
            mAnimationStrategy.initShownView(mErrorView);
            mErrorTextView.setText(errorMessage);
        } else {
            mAnimationStrategy.initHiddenView(mErrorView);
        }
    }

    @MainThread
    public void setLoadingView(View loadingView) {
        this.mLoadingIndicator = Preconditions.checkNotNull(loadingView);
        if (state == State.LOADING) {
            mAnimationStrategy.initShownView(loadingView);
        } else {
            mAnimationStrategy.initHiddenView(loadingView);
        }
    }

    @MainThread
    public void setContentView(View contentView) {
        this.mContentView = Preconditions.checkNotNull(contentView);
        if (state == State.SUCCEEDED) {
            mAnimationStrategy.initShownView(contentView);
        } else {
            mAnimationStrategy.initHiddenView(contentView);
        }
    }

    @AnyThread
    public void onError(@StringRes int errorMessage) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onError()");
        Message message = handler.obtainMessage(MSG_ERROR);
        message.arg1 = errorMessage;
        message.sendToTarget();
    }

    @AnyThread
    public void onSuccess() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onSuccess()");
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            Log.d(TAG, "Called on main thread");
        }
        handler.sendEmptyMessage(MSG_SUCCESS);
    }

    @AnyThread
    public void onLoading() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onLoading()");
        handler.sendEmptyMessage(MSG_LOAD);
    }

    @MainThread
    private void onErrorSync(@StringRes int errorMessage) {
        Preconditions.checkArgument(errorMessage != 0, "errorMessage == 0");
        this.errorMessage = errorMessage;
        if (mErrorTextView != null) mErrorTextView.setText(errorMessage);
        switchToState(State.FAILED);
    }

    @Nullable
    private View getStateView() {
        return getStateView(state);
    }

    @Nullable
    private View getStateView(State state) {
        switch (state) {
            case FAILED:
                return mErrorView;
            case SUCCEEDED:
                return mContentView;
            case LOADING:
                return mLoadingIndicator;
            case FRESH:
                return null;
        }
        throw new IllegalStateException("Unknown state: " + state);
    }

    @MainThread
    private void onSuccessSync() {
        if (BuildConfig.DEBUG) Log.v(TAG, "onSuccessSync()");
        switchToState(State.SUCCEEDED);
    }

    private void switchToState(State newState) {
        if (state == newState) {
            Log.d(TAG, "State not changed: " + newState);
            return;
        }
        View currentView = getStateView();
        View nextView = getStateView(newState);
        if (currentView == null && nextView != null) {
            mAnimationStrategy.showView(nextView);
        } else if (currentView != null && nextView != null) {
            mAnimationStrategy.crossFadeViews(currentView, nextView);
        } else if (currentView != null) {
            mAnimationStrategy.hideView(currentView);
        }
        state = newState;
    }

    @MainThread
    private void onLoadingSync() {
        if (BuildConfig.DEBUG) Log.v(TAG, "onLoadingSync()");
        switchToState(State.LOADING);
    }

    private enum State {
        FAILED, SUCCEEDED, LOADING, FRESH;
    }

    private class Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_ERROR:
                    onErrorSync(message.arg1);
                    return true;
                case MSG_LOAD:
                    onLoadingSync();
                    return true;
                case MSG_SUCCESS:
                    onSuccessSync();
                    return true;
            }
            return false;
        }
    }

}
