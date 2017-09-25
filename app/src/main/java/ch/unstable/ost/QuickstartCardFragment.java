package ch.unstable.ost;


import android.arch.persistence.room.EmptyResultSetException;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;

import ch.unstable.ost.error.ErrorUtils;
import io.reactivex.functions.Consumer;

public abstract class QuickstartCardFragment extends Fragment {

    protected Consumer<Throwable> getErrorConsumer() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (throwable instanceof EmptyResultSetException) {
                    onNoElement();
                } else {
                    onError(throwable);
                }
            }
        };
    }

    private void onNoElement() {
        hideCard();
    }

    @MainThread
    private void onError(final Throwable throwable) {
        hideCard();
        ErrorUtils.showErrorSnackbar(getView(), getErrorMessage(), throwable);
    }

    @StringRes
    abstract protected int getErrorMessage();

    private void hideCard() {
        View view = getView();
        if(view != null) {
            // TODO: animate
            view.setVisibility(View.GONE);
        }
    }

}
