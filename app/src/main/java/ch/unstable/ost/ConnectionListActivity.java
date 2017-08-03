package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.theme.ThemedActivity;

public class ConnectionListActivity extends ThemedActivity
        implements ConnectionListFragment.OnConnectionListInteractionListener,
        BaseNavigationFragment.OnRouteSelectionListener {

    public static final String EXTRA_QUERY = "EXTRA_QUERY";
    private static final String TAG = "ConnectionListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getIntent() == null) {
            finish();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment_container) == null) {
            ConnectionQuery query = getIntent().getParcelableExtra(EXTRA_QUERY);
            ConnectionListFragment connectionListFragment = ConnectionListFragment.newInstance(query);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, connectionListFragment)
                    .commit();

            HeadNavigationFragment fragment = (HeadNavigationFragment) getSupportFragmentManager().findFragmentById(R.id.head_fragment);
            fragment.updateQuery(query);
        }
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        Intent intent = new Intent(this, ConnectionDetailActivity.class);
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, connection);
        startActivity(intent);
    }

    @Override
    public void onRouteSelected(ConnectionQuery query) {
        ConnectionListFragment connectionListFragment = ConnectionListFragment.newInstance(query);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, connectionListFragment)
                .addToBackStack(null)
                .commit();
    }
}
