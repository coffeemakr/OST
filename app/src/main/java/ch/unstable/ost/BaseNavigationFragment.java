package ch.unstable.ost;

import android.content.Context;


import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.unstable.ost.api.model.ConnectionQuery;


public abstract class BaseNavigationFragment extends Fragment {
    private OnRouteSelectionListener mListener;

    @CallSuper
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRouteSelectionListener) {
            mListener = (OnRouteSelectionListener) context;
        } else {
            throw new IllegalStateException(context.toString()
                    + " must implement OnRouteSelectionListener");
        }
    }

    @CallSuper
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void selectRoute(ConnectionQuery query) {
        if (mListener != null) {
            mListener.onRouteSelected(query);
        }
    }

    public interface OnRouteSelectionListener {

        @MainThread
        void onRouteSelected(@NonNull ConnectionQuery query);
    }
}
