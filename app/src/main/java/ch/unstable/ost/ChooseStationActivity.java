package ch.unstable.ost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

import ch.unstable.ost.api.TimetableDAO;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.search.SearchAPI;
import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.theme.ThemedActivity;

public class ChooseStationActivity extends ThemedActivity {

    public static final String EXTRA_CHOOSE_PROMPT = "EXTRA_CHOOSE_PROMPT";
    public static final String EXTRA_RESULT_STATION_NAME = "EXTRA_RESULT_STATION_NAME";
    public static final String EXTRA_RESULT_STATION_ID = "EXTRA_RESULT_STATION_ID";
    private static final int MESSAGE_QUERY_LOCATIONS = 1;
    private static final int MESSAGE_UI_SET_LOCATIONS = 2;
    private EditText mStationEditText;
    private TextWatcher mSuggestionTextWatcher;
    //private TransportAPI transportAPI = new TransportAPI();
    private final TimetableDAO timetableDAO = new SearchAPI();
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private Handler mUIHandler;
    private StationListAdapter mLocationResultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) throw new NullPointerException("toolbar is null");
        setSupportActionBar(toolbar);

        if (getIntent() != null && getIntent().hasExtra(ChooseStationActivity.EXTRA_CHOOSE_PROMPT)) {
            String hint = getIntent().getStringExtra(ChooseStationActivity.EXTRA_CHOOSE_PROMPT);
            TextInputLayout stationNameLayout = (TextInputLayout) findViewById(R.id.stationNameLayout);
            stationNameLayout.setHint(hint);
        }

        mLocationResultAdapter = new StationListAdapter(this);
        mLocationResultAdapter.setOnStationClickListener(new StationListAdapter.OnStationClickListener() {
            public void onStationClicked(Location location) {
                onLocationSelected(location);
            }
        });
        RecyclerView searchedStationView = (RecyclerView) findViewById(R.id.searchedStationView);
        searchedStationView.setAdapter(mLocationResultAdapter);
        searchedStationView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBackgroundHandlerThread = new HandlerThread("ChooseStationActivity.Background");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper(), new BackgroundHandlerCallback());

        mUIHandler = new Handler(new UIHandlerCallback());
        mSuggestionTextWatcher = new SuggestionTextWatcher(mBackgroundHandler);
        mStationEditText = (EditText) findViewById(R.id.stationName);
        mStationEditText.addTextChangedListener(mSuggestionTextWatcher);


        ImageButton clearInputButton = (ImageButton) findViewById(R.id.clearInputButton);
        clearInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStationEditText.setText(null);
            }
        });
    }

    private void onLocationSelected(Location location) {
        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_RESULT_STATION_NAME, location.getName());
        resultData.putExtra(EXTRA_RESULT_STATION_ID, location.getId());
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackgroundHandlerThread.quit();
    }

    private static class SuggestionTextWatcher implements TextWatcher {

        private final Handler mBackgroundHandler;

        public SuggestionTextWatcher(Handler backgroundHandler) {
            this.mBackgroundHandler = backgroundHandler;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBackgroundHandler.removeMessages(MESSAGE_QUERY_LOCATIONS);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String query = s.toString();
            if (!query.isEmpty()) {
                Message message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query);
                mBackgroundHandler.sendMessage(message);
            }
        }
    }

    private class UIHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UI_SET_LOCATIONS:
                    mLocationResultAdapter.setLocations((Location[]) msg.obj);
                    return true;
            }
            return false;
        }
    }

    private class BackgroundHandlerCallback implements Handler.Callback {


        private void handleLocationQuery(String query) {
            Location[] locationList;
            try {
                locationList = timetableDAO.getStationsByQuery(query, new Location.StationType[]{Location.StationType.BUS, Location.StationType.TRAIN});
            } catch (TransportAPI.TooManyRequestsException e) {
                Message message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query);
                mBackgroundHandler.sendMessageDelayed(message, 300);
                return;
            } catch (IOException e) {
                // TODO: error handling
                e.printStackTrace();
                return;
            }
            Message message = mUIHandler.obtainMessage(MESSAGE_UI_SET_LOCATIONS, locationList);
            mUIHandler.sendMessage(message);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_QUERY_LOCATIONS:
                    handleLocationQuery((String) msg.obj);
                    return true;
            }
            return false;
        }
    }
}
