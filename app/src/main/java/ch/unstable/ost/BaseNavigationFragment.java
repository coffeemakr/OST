package ch.unstable.ost;

import android.content.Context;
import android.support.v4.app.Fragment;


public abstract class BaseNavigationFragment extends Fragment {
    private OnRouteSelectionListener mListener;

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void selectRoute(String start, String destination) {
        if(mListener != null) {
            mListener.onRouteSelected(start, destination);
        }
    }

    public interface OnRouteSelectionListener {
        void onRouteSelected(String start, String destination);
    }
}
