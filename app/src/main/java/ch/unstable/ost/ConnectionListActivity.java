package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;

import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.ConnectionQuery;

public class ConnectionListActivity extends AppCompatActivity  implements ConnectionListFragment.OnConnectionListInteractionListener{

    public static final String EXTRA_START = "EXTRA_START";
    public static final String EXTRA_DESTINATION = "EXTRA_DESTINATION";
    public static final String EXTRA_VIAS = "EXTRA_VIAS";
    private static final String TAG = "ConnectionListActivity";
    public static final String EXTRA_START_TIME = "EXTRA_START_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        if(getIntent() == null) {
            finish();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.fragment_container) == null) {
            ConnectionListFragment connectionListFragment = getConnectionListFragment(getIntent());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, connectionListFragment)
                    .commit();
        }
    }

    private ConnectionListFragment getConnectionListFragment(Intent intent) {
        String start = intent.getStringExtra(EXTRA_START);
        String destination = intent.getStringExtra(EXTRA_DESTINATION);


        ConnectionQuery.Builder queryBuilder = new ConnectionQuery.Builder()
                .setFrom(start)
                .setTo(destination);

        if(intent.hasExtra(EXTRA_START_TIME)) {
            Date startTime = new Date(intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis()));
            queryBuilder.setStarTime(startTime);
        }

        return ConnectionListFragment.newInstance(queryBuilder.build());
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        Intent intent = new Intent(this, ConnectionDetailActivity.class);
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, connection);
        startActivity(intent);
    }
}
