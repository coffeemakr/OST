package ch.unstable.ost;


import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.room.EmptyResultSetException;

import ch.unstable.ost.error.ErrorUtils;
import io.reactivex.functions.Consumer;

public abstract class QuickstartCardFragment extends Fragment {

    protected Consumer<Throwable> getErrorConsumer() {
        return throwable -> {
            if (throwable instanceof EmptyResultSetException) {
                onNoElement();
            } else {
                onError(throwable);
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
        if (view != null) {
            // TODO: animate
            view.setVisibility(View.GONE);
        }
    }

}
