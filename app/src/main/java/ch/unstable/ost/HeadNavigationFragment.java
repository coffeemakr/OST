package ch.unstable.ost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.unstable.ost.api.transport.model.ConnectionQuery;


public class HeadNavigationFragment extends Fragment{

    private static final int REQUEST_CODE_CHOOSE_TO = 1;
    private static final int REQUEST_CODE_CHOOSE_FROM = 2;
    private static final String KEY_QUERY = "KEY_QUERY";
    private OnNavigationChangeListener mOnNavigationChangeListener;
    private View.OnClickListener mOnButtonClickListener;
    private ConnectionQuery.Builder mConnectionQueryBuilder;

    @NonNull
    public static HeadNavigationFragment newInstance() {
        return new HeadNavigationFragment();
    }

    public HeadNavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnButtonClickListener = new OnNavigationButtonsClickListener();

        mConnectionQueryBuilder = null;
        if(savedInstanceState != null) {
            ConnectionQuery savedQuery = savedInstanceState.getParcelable(KEY_QUERY);
            if(savedQuery != null) {
                mConnectionQueryBuilder = new ConnectionQuery.Builder(savedQuery);
            }
        } else {
            if(getArguments() != null) {
                ConnectionQuery connectionQuery = getArguments().getParcelable(KEY_QUERY);
                if(connectionQuery != null) {
                    mConnectionQueryBuilder = new ConnectionQuery.Builder(connectionQuery);
                }
            }
        }
        if(mConnectionQueryBuilder == null) {
            mConnectionQueryBuilder = new ConnectionQuery.Builder();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mConnectionQueryBuilder != null) {
            outState.putParcelable(KEY_QUERY, mConnectionQueryBuilder.build());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!(context instanceof OnNavigationChangeListener)) {
            throw new IllegalStateException("context must implement OnNavigationChangeListener");
        }
        mOnNavigationChangeListener = (OnNavigationChangeListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNavigationChangeListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.navigation_buttons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button fromButton = (Button) view.findViewById(R.id.fromButton);
        Button toButton = (Button) view.findViewById(R.id.toButton);
        fromButton.setOnClickListener(mOnButtonClickListener);
        toButton.setOnClickListener(mOnButtonClickListener);
    }

    public interface OnNavigationChangeListener {
        void onNavigationSelected(ConnectionQuery newQuery);
    }

    private void startStationChooser(@StringRes int chooseRequest, int codeTo) {
        String chooseString = getContext().getString(chooseRequest);
        Intent intent = new Intent(getContext(), ChooseStationActivity.class);
        intent.setAction(Intent.ACTION_RUN);
        intent.putExtra(ChooseStationActivity.EXTRA_CHOOSE_PROMPT, chooseString);
        startActivityForResult(intent, codeTo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String name;
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_FROM:
                    name = data.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME);
                    mConnectionQueryBuilder.setFrom(name);
                    onQueryChanged();
                    break;
                case REQUEST_CODE_CHOOSE_TO:
                    name = data.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME);
                    mConnectionQueryBuilder.setTo(name);
                    onQueryChanged();
                    break;
            }
        }
    }

    private void onQueryChanged() {
        if(mOnNavigationChangeListener != null) {
            mOnNavigationChangeListener.onNavigationSelected(mConnectionQueryBuilder.build());
        }
    }

    public class OnNavigationButtonsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toButton:
                    startStationChooser(R.string.request_choose_to, REQUEST_CODE_CHOOSE_TO);
                case R.id.fromButton:
                    startStationChooser(R.string.request_choose_from, REQUEST_CODE_CHOOSE_FROM);

            }
        }
    }
}
