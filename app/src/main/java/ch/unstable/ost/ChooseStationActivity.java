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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import ch.unstable.ost.api.StationsDAO;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.preference.SettingsActivity;
import ch.unstable.ost.preference.StationDaoLoader;
import ch.unstable.ost.theme.ThemedActivity;

public class ChooseStationActivity extends ThemedActivity {

    public static final String EXTRA_CHOOSE_PROMPT = "EXTRA_CHOOSE_PROMPT";
    public static final String EXTRA_RESULT_STATION_NAME = "EXTRA_RESULT_STATION_NAME";
    public static final String EXTRA_RESULT_STATION_ID = "EXTRA_RESULT_STATION_ID";
    private static final Location.StationType[] STATION_TYPES = {
            Location.StationType.BUS,
            Location.StationType.TRAIN,
            Location.StationType.TRAM};
    private static final int MESSAGE_QUERY_LOCATIONS = 1;
    private static final int MESSAGE_UI_SET_LOCATIONS = 2;
    private static final int MESSAGE_ERROR = 3;
    private static final String TAG = ChooseStationActivity.class.getSimpleName();
    private static final int MESSAGE_LOADING_STARTED = 4;
    //private TransportAPI transportAPI = new TransportAPI();
    private StationsDAO stationsDAO;
    private EditText mStationEditText;
    private TextWatcher mSuggestionTextWatcher;
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private Handler mUIHandler;
    private StationListAdapter mLocationResultAdapter;
    private ProgressBar mProgressBar;


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

        if (stationsDAO == null) {
            stationsDAO = StationDaoLoader.createStationDAO(this);
        }
        mLocationResultAdapter = new StationListAdapter(this);
        mLocationResultAdapter.setOnStationClickListener(new StationListAdapter.OnStationClickListener() {
            public void onStationClicked(Location location) {
                onLocationSelected(location);
            }
        });


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setIndeterminate(true);

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
        mStationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String location = v.getText().toString();
                    if (location.isEmpty()) {
                        return false;
                    }
                    onLocationSelected(location);
                    return true;
                }
                return false;
            }
        });

        ImageButton clearInputButton = (ImageButton) findViewById(R.id.clearInputButton);
        clearInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStationEditText.setText(null);
            }
        });
    }

    private void onLocationSelected(String location) {
        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_RESULT_STATION_NAME, location);
        resultData.putExtra(EXTRA_RESULT_STATION_ID, location);
        setResult(Activity.RESULT_OK, resultData);
        finish();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void showProgressBar() {
        final Animation currentAnimation = mProgressBar.getAnimation();
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        if (mProgressBar.getVisibility() != View.VISIBLE) {
            mProgressBar.setAlpha(0);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.animate()
                .setDuration(500)
                .alpha(1f)
                .start();
    }

    private void hideProgressBar() {
        final Animation currentAnimation = mProgressBar.getAnimation();
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }

        if (mProgressBar.getAlpha() == 0f || mProgressBar.getVisibility() != View.VISIBLE) {
            return;
        }
        mProgressBar.animate()
                .setDuration(500)
                .alpha(0f)
                .start();
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
                    hideProgressBar();
                    mLocationResultAdapter.setLocations((Location[]) msg.obj);
                    return true;
                case MESSAGE_ERROR:
                    // TODO: show error message
                    hideProgressBar();
                    return true;
                case MESSAGE_LOADING_STARTED:
                    showProgressBar();
                    return true;

            }
            return false;
        }
    }

    private class BackgroundHandlerCallback implements Handler.Callback {
        private void handleLocationQuery(String query) {
            mUIHandler.sendEmptyMessage(MESSAGE_LOADING_STARTED);
            Location[] locationList;
            try {
                locationList = stationsDAO.getStationsByQuery(query, STATION_TYPES);
            } catch (TransportAPI.TooManyRequestsException e) {
                Message message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query);
                mBackgroundHandler.sendMessageDelayed(message, 300);
                return;
            } catch (IOException e) {
                mUIHandler.sendEmptyMessage(MESSAGE_ERROR);
                Log.e(TAG, "Failed to get suggestions", e);
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
