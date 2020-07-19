package ch.unstable.ost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.MainThread
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.QueryHistoryDao
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.views.lists.query.QueryBinder.bindQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class LastQueryCardFragment : QuickstartCardFragment() {
    private lateinit var mCardLastQuery: View
    private lateinit var mQueryDao: QueryHistoryDao
    private lateinit var mLastQueryFromTo: TextView
    private lateinit var mLastQueryDate: TextView
    private var mOnQuerySelectedListener: OnQuerySelectedListener? = null
    private var mLastQuery: QueryHistory? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQueryDao = Databases.getCacheDatabase(context).queryHistoryDao()
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mOnQuerySelectedListener = context as OnQuerySelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        mOnQuerySelectedListener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_last_query, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCardLastQuery = view.findViewById(R.id.cardLastQuery)
        mCardLastQuery.visibility = View.GONE
        mLastQueryFromTo = view.findViewById(R.id.lastQueryFromTo)
        mLastQueryDate = view.findViewById(R.id.lastQueryDate)
        val buttonMore = view.findViewById<Button>(R.id.buttonMore)
        buttonMore.setOnClickListener { onShowMoreQueries() }
        val buttonLoad = view.findViewById<Button>(R.id.buttonOpen)
        buttonLoad.setOnClickListener { onOpenQuery() }
    }

    private fun onOpenQuery() {
        if (mOnQuerySelectedListener == null) {
            Log.w(TAG, "mOnQuerySelectedListener is null")
            return
        }
        mLastQuery ?: error("mLastQuery is null")
        mOnQuerySelectedListener!!.onRouteSelected(mLastQuery!!.query)
    }

    private fun onShowMoreQueries() {
        val intent = Intent(context, QueryHistoryActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val disposable = mQueryDao.latestQuery
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { queryHistory: QueryHistory -> updateLatestQuery(queryHistory) }, errorConsumer)
        mCompositeDisposable!!.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable!!.dispose()
    }

    @MainThread
    private fun updateLatestQuery(queryHistory: QueryHistory) {
        mCardLastQuery.visibility = View.VISIBLE // TODO animate
        mLastQuery = queryHistory
        bindQuery(queryHistory, mLastQueryDate, mLastQueryFromTo)
    }

    override fun getErrorMessage(): Int {
        return R.string.error_failed_to_load_last_query
    }

    interface OnQuerySelectedListener {
        @MainThread
        fun onRouteSelected(query: ConnectionQuery)
    }

    companion object {
        private const val TAG = "LastQueryCardFragment"
    }
}