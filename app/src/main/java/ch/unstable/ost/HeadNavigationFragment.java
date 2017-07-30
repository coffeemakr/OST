package ch.unstable.ost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.unstable.ost.api.transport.model.ConnectionQuery;


public class HeadNavigationFragment extends BaseNavigationFragment {

    private static final int REQUEST_CODE_CHOOSE_TO = 1;
    private static final int REQUEST_CODE_CHOOSE_FROM = 2;
    private static final String KEY_QUERYBUILDER = "KEY_QUERY";
    private View.OnClickListener mOnButtonClickListener;
    private ConnectionQuery.Builder mConnectionQueryBuilder;
    private Button mToButton;
    private Button mFromButton;

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
            mConnectionQueryBuilder = savedInstanceState.getParcelable(KEY_QUERYBUILDER);
        } else if (getArguments() != null) {
            mConnectionQueryBuilder = getArguments().getParcelable(KEY_QUERYBUILDER);
        }
        if(mConnectionQueryBuilder == null) {
            mConnectionQueryBuilder = new ConnectionQuery.Builder();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mConnectionQueryBuilder != null) {
            outState.putParcelable(KEY_QUERYBUILDER, mConnectionQueryBuilder);
        }
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
        mFromButton = (Button) view.findViewById(R.id.fromButton);
        mToButton = (Button) view.findViewById(R.id.toButton);
        mFromButton.setOnClickListener(mOnButtonClickListener);
        mToButton.setOnClickListener(mOnButtonClickListener);
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
                    setFrom(name);
                    break;
                case REQUEST_CODE_CHOOSE_TO:
                    name = data.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME);
                    setTo(name);
                    break;
            }
        }
    }

    private void setTo(String to) {
        mConnectionQueryBuilder.setTo(to);
        mToButton.setText(to);
        onQueryChanged();
    }

    private void setFrom(String from) {
        mConnectionQueryBuilder.setFrom(from);
        mFromButton.setText(from);
        onQueryChanged();
    }

    private void onQueryChanged() {
        if(mConnectionQueryBuilder.getTo() != null && mConnectionQueryBuilder.getFrom() != null) {
            selectRoute(mConnectionQueryBuilder.build());
        }
    }

    public class OnNavigationButtonsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toButton:
                    startStationChooser(R.string.request_choose_to, REQUEST_CODE_CHOOSE_TO);
                    break;
                case R.id.fromButton:
                    startStationChooser(R.string.request_choose_from, REQUEST_CODE_CHOOSE_FROM);
                    break;
            }
        }
    }
}
