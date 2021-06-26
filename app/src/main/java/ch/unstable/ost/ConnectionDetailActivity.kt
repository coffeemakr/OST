package ch.unstable.ost

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MainThread
import androidx.appcompat.widget.Toolbar
import ch.unstable.ost.ConnectionDetailFragment.OnConnectionDetailInteractionListener
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.Section
import ch.unstable.ost.preference.SettingsActivity
import ch.unstable.ost.theme.ThemedActivity
import ch.unstable.ost.utils.NavHelper.openAbout

class ConnectionDetailActivity : ThemedActivity(), OnConnectionDetailInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            val connection: Connection = intent.getParcelableExtra(EXTRA_CONNECTION)
            val favoriteId = intent.getLongExtra(EXTRA_FAVORITE_ID, 0L)
            val fragment = ConnectionDetailFragment.newInstance(connection, favoriteId)
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }
    }

    @MainThread
    override fun onSectionSelected(section: Section) {
        val fragment = SectionDetailFragment.newInstance(section)
        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
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

    companion object {
        const val EXTRA_CONNECTION = "EXTRA_CONNECTION"
        const val EXTRA_FAVORITE_ID = "EXTRA_FAVORITE_ID"
    }
}