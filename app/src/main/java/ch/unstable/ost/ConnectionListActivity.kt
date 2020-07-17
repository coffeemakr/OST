package ch.unstable.ost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import ch.unstable.ost.BaseNavigationFragment.OnRouteSelectionListener
import ch.unstable.ost.ConnectionListFragment.Companion.newInstance
import ch.unstable.ost.ConnectionListFragment.OnConnectionListInteractionListener
import ch.unstable.ost.FavoriteCardFragment.OnFavoriteSelectedListener
import ch.unstable.ost.LastQueryCardFragment.OnQuerySelectedListener
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.database.model.FavoriteConnection
import ch.unstable.ost.preference.SettingsActivity
import ch.unstable.ost.theme.ThemedActivity
import ch.unstable.ost.utils.NavHelper.openAbout
import com.google.common.base.Objects
import com.google.common.base.Preconditions
import java.util.*

class ConnectionListActivity : ThemedActivity(), OnConnectionListInteractionListener, OnRouteSelectionListener, OnQuerySelectedListener, OnFavoriteSelectedListener {
    companion object {
        private const val CLASS_NAME = "ch.unstable.ost.ConnectionListActivity"
        const val EXTRA_CONNECTION_FROM = "$CLASS_NAME.EXTRA_CONNECTION_FROM"
        const val EXTRA_CONNECTION_TO = "$CLASS_NAME.EXTRA_CONNECTION_TO"
        const val EXTRA_CONNECTION_QUERY = "$CLASS_NAME.EXTRA_CONNECTION_QUERY"
        private const val TAG = "ConnectionListActivity"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    private var mCurrentlyShownQuery: ConnectionQuery? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, QuickstartFragment())
                    .commit()
        }
        if (intent != null) {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Preconditions.checkNotNull(intent, "intent is null")
        if (Intent.ACTION_SEARCH != intent.action) {
            return
        }
        var query: ConnectionQuery? = null
        if (intent.hasExtra(EXTRA_CONNECTION_QUERY)) {
            query = intent.getParcelableExtra(EXTRA_CONNECTION_QUERY)
        } else if (intent.hasExtra(EXTRA_CONNECTION_FROM) && intent.hasExtra(EXTRA_CONNECTION_TO)) {
            query = ConnectionQuery(
                    intent.getStringExtra(EXTRA_CONNECTION_FROM),
                    intent.getStringExtra(EXTRA_CONNECTION_TO),
                    ArrayList(), null, null)
        }
        query?.let { onRouteSelected(it) }
    }

    override fun onConnectionSelected(connection: Connection) {
        val intent = Intent(this, ConnectionDetailActivity::class.java)
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, connection)
        startActivity(intent)
    }

    override fun onQueryStarted(query: ConnectionQuery) {
        Log.d(TAG, "onQueryStarted: $query")
    }

    @MainThread
    override fun onRouteSelected(query: ConnectionQuery) {
        Log.d(TAG, "On route selected: $query")
        if (Objects.equal(mCurrentlyShownQuery, query)) {
            Log.d(TAG, "Query has not changed")
            return
        }
        mCurrentlyShownQuery = query
        val connectionListFragment = newInstance(query)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, connectionListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        updateHeadQuery(query)
    }

    private fun updateHeadQuery(query: ConnectionQuery?) {
        val headNavigationFragment = supportFragmentManager.findFragmentById(R.id.head_fragment) as HeadNavigationFragment?
        if (headNavigationFragment != null) {
            if (query == null) {
                headNavigationFragment.clearQuery()
            } else {
                headNavigationFragment.updateQuery(query)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        mCurrentlyShownQuery = if (fragment != null && fragment is ConnectionListFragment) {
            val query = fragment.connectionQuery
            updateHeadQuery(query)
            query
        } else {
            updateHeadQuery(null)
            null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_about -> {
                openAbout(this)
                true
            }
            else -> false
        }
    }

    override fun onFavoriteSelected(favoriteConnection: FavoriteConnection) {
        val intent = Intent(this, ConnectionDetailActivity::class.java)
        intent.putExtra(ConnectionDetailActivity.EXTRA_CONNECTION, favoriteConnection.connection)
        intent.putExtra(ConnectionDetailActivity.EXTRA_FAVORITE_ID, favoriteConnection.id)
        startActivity(intent)
    }
}