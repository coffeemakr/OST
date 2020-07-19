package ch.unstable.ost

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.api.model.Station
import ch.unstable.ost.api.model.Station.StationType
import ch.unstable.ost.preference.SettingsActivity
import ch.unstable.ost.preference.StationDaoLoader
import ch.unstable.ost.theme.ThemedActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_choose_station.*
import java.io.IOException

class ChooseStationActivity : ThemedActivity() {
    private lateinit var locationResultAdapter: StationListAdapter

    //private TransportAPI transportAPI = new TransportAPI();
    private var stationsDAO: StationsDAO? = null
    private var mBackgroundHandlerThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mUIHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_station)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
                ?: throw NullPointerException("toolbar is null")
        setSupportActionBar(toolbar)
        if (intent != null && intent.hasExtra(EXTRA_CHOOSE_PROMPT)) {
            val hint = intent.getStringExtra(EXTRA_CHOOSE_PROMPT)
            val stationNameLayout = findViewById<TextInputLayout>(R.id.stationNameLayout)
            stationNameLayout.hint = hint
        }
        if (stationsDAO == null) {
            stationsDAO = StationDaoLoader.createStationDAO(this)
        }

        locationResultAdapter = StationListAdapter(this)
        locationResultAdapter.setOnStationClickListener(this@ChooseStationActivity::onLocationSelected)

        progressBar.isIndeterminate = true
        searchedStationView.adapter = locationResultAdapter
        searchedStationView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBackgroundHandlerThread = HandlerThread("ChooseStationActivity.Background").also {
            it.start()
        }
        mBackgroundHandler = Handler(mBackgroundHandlerThread!!.looper, BackgroundHandlerCallback())
        mUIHandler = Handler(UIHandlerCallback())
        val suggestionTextWatcher: TextWatcher = SuggestionTextWatcher(mBackgroundHandler!!)

        stationName.addTextChangedListener(suggestionTextWatcher)
        stationName.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val location = v.text.toString()
                if (location.isEmpty()) {
                    return@OnEditorActionListener  false
                }
                onLocationSelected(location)
                return@OnEditorActionListener  true
            }
            return@OnEditorActionListener false
        })
        val clearInputButton = findViewById<ImageButton>(R.id.clearInputButton)
        clearInputButton.setOnClickListener { stationName?.text = null }
    }

    private fun onLocationSelected(location: String) {
        val resultData = Intent()
        resultData.putExtra(EXTRA_RESULT_STATION_NAME, location)
        resultData.putExtra(EXTRA_RESULT_STATION_ID, location)
        setResult(Activity.RESULT_OK, resultData)
        finish()
    }

    private fun onLocationSelected(station: Station) {
        val resultData = Intent()
        resultData.putExtra(EXTRA_RESULT_STATION_NAME, station.name)
        resultData.putExtra(EXTRA_RESULT_STATION_ID, station.id)
        setResult(Activity.RESULT_OK, resultData)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBackgroundHandlerThread!!.quit()
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
            else -> false
        }
    }

    private fun showProgressBar() {
        progressBar?.let {
            val currentAnimation = it.animation
            currentAnimation?.cancel()
            if (it.visibility != View.VISIBLE) {
                it.alpha = 0f
                it.visibility = View.VISIBLE
            }
            it.animate()
                    .setDuration(500)
                    .alpha(1f)
                    .start()
        }
    }

    private fun hideProgressBar() {
        progressBar?.let {
            val currentAnimation = it.animation
            currentAnimation?.cancel()
            if (it.alpha == 0f || it.visibility != View.VISIBLE) {
                return
            }
            it.animate()
                    .setDuration(500)
                    .alpha(0f)
                    .start()
        }
    }

    private class SuggestionTextWatcher(private val mBackgroundHandler: Handler) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            mBackgroundHandler.removeMessages(MESSAGE_QUERY_LOCATIONS)
        }

        override fun afterTextChanged(s: Editable) {
            val query = s.toString()
            if (query.isNotEmpty()) {
                val message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query)
                mBackgroundHandler.sendMessage(message)
            }
        }

    }

    private inner class UIHandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                MESSAGE_UI_SET_LOCATIONS -> {
                    hideProgressBar()
                    locationResultAdapter.locations = msg.obj as List<Station>
                    return true
                }
                MESSAGE_ERROR -> {
                    // TODO: show error message
                    hideProgressBar()
                    return true
                }
                MESSAGE_LOADING_STARTED -> {
                    showProgressBar()
                    return true
                }
            }
            return false
        }
    }

    private inner class BackgroundHandlerCallback : Handler.Callback {
        private fun handleLocationQuery(query: String) {
            mUIHandler!!.sendEmptyMessage(MESSAGE_LOADING_STARTED)
            val stationList: List<Station>
            stationList = try {
                stationsDAO!!.getStationsByQuery(query, STATION_TYPES)
                //} catch (TooManyRequestsException e) {
                //   Message message = mBackgroundHandler.obtainMessage(MESSAGE_QUERY_LOCATIONS, query);
                // mBackgroundHandler.sendMessageDelayed(message, 300);
                //  return;
            } catch (e: IOException) {
                mUIHandler!!.sendEmptyMessage(MESSAGE_ERROR)
                Log.e(TAG, "Failed to get suggestions", e)
                Log.e(TAG, e.message)
                return
            }
            val message = mUIHandler!!.obtainMessage(MESSAGE_UI_SET_LOCATIONS, stationList)
            mUIHandler!!.sendMessage(message)
        }

        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                MESSAGE_QUERY_LOCATIONS -> {
                    handleLocationQuery(msg.obj as String)
                    return true
                }
            }
            return false
        }
    }

    companion object {
        const val EXTRA_CHOOSE_PROMPT = "EXTRA_CHOOSE_PROMPT"
        const val EXTRA_RESULT_STATION_NAME = "EXTRA_RESULT_STATION_NAME"
        const val EXTRA_RESULT_STATION_ID = "EXTRA_RESULT_STATION_ID"
        private val STATION_TYPES = listOf(
                StationType.BUS,
                StationType.TRAIN,
                StationType.TRAM)
        private const val MESSAGE_QUERY_LOCATIONS = 1
        private const val MESSAGE_UI_SET_LOCATIONS = 2
        private const val MESSAGE_ERROR = 3
        private val TAG = ChooseStationActivity::class.java.simpleName
        private const val MESSAGE_LOADING_STARTED = 4
    }
}