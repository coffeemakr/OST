package ch.unstable.ost;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;

import ch.unstable.ost.api.transport.model.ConnectionQuery;


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

    public void selectRoute(String start, String destination) {
        ConnectionQuery query = new ConnectionQuery.Builder()
                .setFrom(start)
                .setTo(destination)
                .build();
        selectRoute(query);
    }

    public void selectRoute(ConnectionQuery query) {
        if (mListener != null) {
            mListener.onRouteSelected(query);
        }
    }

    public interface OnRouteSelectionListener {

        void onRouteSelected(ConnectionQuery query);
    }
}
