package ch.unstable.ost;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.List;

import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.api.transport.model.Location;

public class ChooseStationActivity extends AppCompatActivity {

    public static final String EXTRA_CHOOSE_PROMPT = "EXTRA_CHOOSE_PROMPT";
    public static final String EXTRA_RESULT_STATION_NAME = "EXTRA_RESULT_STATION_NAME";
    private static final int MESSAGE_QUERY_LOCATIONS = 1;
    private static final int MESSAGE_UI_SET_LOCATIONS = 2;
    private EditText mStationEditText;
    private TextWatcher mSuggestionTextWatcher;
    private TransportAPI transportAPI = new TransportAPI();
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private Handler mUIHandler;
    private StationListAdapter mLocationResultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        mLocationResultAdapter = new StationListAdapter();
        RecyclerView searchedStationView = (RecyclerView) findViewById(R.id.searchedStationView);
        searchedStationView.setAdapter(mLocationResultAdapter);
        searchedStationView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBackgroundHandlerThread = new HandlerThread("ChooseStationActivity.Background");
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
            mBackgroundHandler.removeMessages(MESSAGE_QUERY_LOCATIONS);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String query = s.toString();
            Message message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query);
            mBackgroundHandler.sendMessage(message);
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
            List<Location> locationList;
            Location[] locationArray;
            try {
                locationList = transportAPI.getLocationsByQuery(query);
            } catch (IOException e) {
                // TODO: error handling
                e.printStackTrace();
                return;
            }
            locationArray = locationList.toArray(new Location[locationList.size()]);
            Message message = mUIHandler.obtainMessage(MESSAGE_UI_SET_LOCATIONS, locationArray);
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
