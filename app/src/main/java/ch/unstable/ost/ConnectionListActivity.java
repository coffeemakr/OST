package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.ConnectionQuery;

public class ConnectionListActivity extends AppCompatActivity  implements ConnectionListFragment.OnConnectionListInteractionListener{

    private static final String TAG = "ConnectionListActivity";
    public static final String EXTRA_QUERY = "EXTRA_QUERY";

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
        ConnectionQuery query = intent.getParcelableExtra(EXTRA_QUERY);
        return ConnectionListFragment.newInstance(query);
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        Intent intent = new Intent(this, ConnectionDetailActivity.class);
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, connection);
        startActivity(intent);
    }
}
