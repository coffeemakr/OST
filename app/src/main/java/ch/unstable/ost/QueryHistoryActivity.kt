package ch.unstable.ost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.ConnectionListActivity
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.QueryHistoryDao
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.views.lists.query.QueryHistoryAdapter
import com.google.common.base.Preconditions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

class QueryHistoryActivity : AppCompatActivity() {
    private var mQueryHistoryDao: QueryHistoryDao? = null
    private var mQueryHistoryAdapter: QueryHistoryAdapter? = null
    private val mOnQueryHistoryItemClickListener = View.OnClickListener { view ->
        val query = view.tag as QueryHistory
        Preconditions.checkNotNull(query, "query")
        openConnection(query)
    }
    private var mCompositeDisposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCompositeDisposable = CompositeDisposable()
        setContentView(R.layout.activity_query_history)
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mQueryHistoryDao = Databases.getCacheDatabase(this).queryHistoryDao()
        if (mQueryHistoryAdapter == null) {
            mQueryHistoryAdapter = QueryHistoryAdapter(mOnQueryHistoryItemClickListener)
        }
        val historyEntries = findViewById<RecyclerView>(R.id.historyEntries)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        historyEntries.layoutManager = layoutManager
        historyEntries.adapter = mQueryHistoryAdapter
        val divider = DividerItemDecoration(
                historyEntries.context,
                layoutManager.orientation
        )
        historyEntries.addItemDecoration(divider)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConnection(query: QueryHistory) {
        val intent = Intent(this, ConnectionListActivity::class.java)
        intent.putExtra(ConnectionListActivity.EXTRA_CONNECTION_QUERY, query.query)
        intent.action = Intent.ACTION_SEARCH
        startActivity(intent)
    }

    public override fun onResume() {
        super.onResume()
        val disposable = mQueryHistoryDao!!.connections
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(queriesConsumer)
        mCompositeDisposable!!.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable!!.dispose()
    }

    /**
     * Get a consumer which adds the loaded queries into the adapter
     *
     * @return the consumer
     */
    private val queriesConsumer: Consumer<List<QueryHistory>>
        get() = Consumer { entries: List<QueryHistory> ->
            if (BuildConfig.DEBUG) Log.d(TAG, "Got entries: $entries")
            if (mQueryHistoryAdapter != null) {
                mQueryHistoryAdapter!!.setEntries(entries)
            } else {
                Log.w(TAG, "Adapter is null")
            }
        }

    companion object {
        private const val TAG = "QueryHistoryActivity"
    }
}