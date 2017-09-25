package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Objects;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.preference.SettingsActivity;
import ch.unstable.ost.theme.ThemedActivity;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionListActivity extends ThemedActivity
        implements ConnectionListFragment.OnConnectionListInteractionListener,
        BaseNavigationFragment.OnRouteSelectionListener,
        LastQueryCardFragment.OnQuerySelectedListener,
        FavoriteCardFragment.OnFavoriteSelectedListener {

    private static final String CLASS_NAME = "ch.unstable.ost.ConnectionListActivity";
    public static final String EXTRA_CONNECTION_FROM = CLASS_NAME + ".EXTRA_CONNECTION_FROM";
    public static final String EXTRA_CONNECTION_TO = CLASS_NAME + ".EXTRA_CONNECTION_TO";
    public static final String EXTRA_CONNECTION_QUERY = CLASS_NAME + ".EXTRA_CONNECTION_QUERY";
    private static final String TAG = "ConnectionListActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    private ConnectionQuery mCurrentlyShownQuery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new QuickstartFragment())
                    .commit();
        }

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(@NonNull Intent intent) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(intent, "intent is null");
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return;
        }
        ConnectionQuery query = null;
        if (intent.hasExtra(EXTRA_CONNECTION_QUERY)) {
            query = intent.getParcelableExtra(EXTRA_CONNECTION_QUERY);
        } else if (intent.hasExtra(EXTRA_CONNECTION_FROM) && intent.hasExtra(EXTRA_CONNECTION_TO)) {
            query = new ConnectionQuery.Builder()
                    .setTo(intent.getStringExtra(EXTRA_CONNECTION_TO))
                    .setFrom(intent.getStringExtra(EXTRA_CONNECTION_FROM))
                    .build();
        }
        if (query != null) {
            onRouteSelected(query);
        }
    }

    @Override
    public void onConnectionSelected(@NonNull Connection connection) {
        Intent intent = new Intent(this, ConnectionDetailActivity.class);
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, connection);
        startActivity(intent);
    }

    @Override
    public void onQueryStarted(@NonNull ConnectionQuery query) {
        Log.d(TAG, "onQueryStarted: " + query);
    }

    @MainThread
    @Override
    public void onRouteSelected(@NonNull ConnectionQuery query) {
        Log.d(TAG, "On route selected: " + query);
        if (Objects.equal(mCurrentlyShownQuery, query)) {
            Log.d(TAG, "Query has not changed");
            return;
        }
        mCurrentlyShownQuery = query;
        ConnectionListFragment connectionListFragment = ConnectionListFragment.newInstance(query);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, connectionListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
        updateHeadQuery(query);
    }

    private void updateHeadQuery(@Nullable ConnectionQuery query) {
        HeadNavigationFragment headNavigationFragment = (HeadNavigationFragment) getSupportFragmentManager().findFragmentById(R.id.head_fragment);
        if (headNavigationFragment != null) {
            if (query == null) {
                headNavigationFragment.clearQuery();
            } else {
                headNavigationFragment.updateQuery(query);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && (fragment instanceof ConnectionListFragment)) {
            ConnectionListFragment connectionListFragment = (ConnectionListFragment) fragment;
            ConnectionQuery query = connectionListFragment.getConnectionQuery();
            updateHeadQuery(query);
            mCurrentlyShownQuery = query;
        } else {
            updateHeadQuery(null);
            mCurrentlyShownQuery = null;
        }
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
            default:
                return false;
        }
    }

    @Override
    public void onFavoriteSelected(@NonNull FavoriteConnection favoriteConnection) {
        
    }

    public static class EmptyConnectionListFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty_connection_list, container, false);
        }
    }
}
