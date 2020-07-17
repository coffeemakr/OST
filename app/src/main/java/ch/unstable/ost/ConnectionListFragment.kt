package ch.unstable.ost

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.lib.sbb.SbbApiFactory
import ch.unstable.ost.api.ConnectionAPI
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.ConnectionPage
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.QueryHistoryDao
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.utils.NavHelper.startErrorActivity
import ch.unstable.ost.views.NoAnimationStrategy
import ch.unstable.ost.views.ViewStateHolder
import ch.unstable.ost.views.lists.connection.ConnectionListAdapter
import ch.unstable.ost.views.lists.connection.ConnectionListAdapter.OnConnectionClickListener
import com.google.common.base.Preconditions
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_connection_list.*

class ConnectionListFragment : Fragment() {
    private val mOnConnectionClickListener = OnConnectionSelectedCaller()
    private var connectionAPI: ConnectionAPI? = null
    private var mConnectionAdapter: ConnectionListAdapter? = null

    /**
     * Get the currently shown connection query
     *
     * @return the query
     */
    lateinit var connectionQuery: ConnectionQuery
    private var mOnConnectionListInteractionListener: OnConnectionListInteractionListener? = null
    private lateinit var mConnectionListScrollListener: RecyclerView.OnScrollListener
    private lateinit var mQueryHistoryDao: QueryHistoryDao
    lateinit var  mViewStateHolder: ViewStateHolder
    lateinit var mCompositeDisposable: CompositeDisposable
    private val mOverScrollListener: ConnectionListAdapter.Listener = object : ConnectionListAdapter.Listener {
        override fun onLoadBelow(adapter: ConnectionListAdapter?, pageToLoad: Int): Boolean {
            return false
        }

        override fun onLoadAbove(adapter: ConnectionListAdapter?, pageToLoad: Int): Boolean {
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionAPI = SbbApiFactory().createAPI(SbbApiFactory().createSslContext(context!!))
        mCompositeDisposable = CompositeDisposable()
        val database = Databases.getCacheDatabase(context)
        mQueryHistoryDao = database.queryHistoryDao()

        // Empty constructor
        mViewStateHolder = ViewStateHolder(NoAnimationStrategy())
        mViewStateHolder.setOnRetryClickListener { loadConnections() }
        mConnectionAdapter = ConnectionListAdapter()
        mConnectionAdapter!!.setOnLoadMoreListener(mOverScrollListener)
        mConnectionAdapter!!.setOnConnectionClickListener(mOnConnectionClickListener)
        connectionQuery = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(ARG_QUERY)
        } else {
            arguments?.getParcelable(ARG_QUERY) ?: error("no save or argumetns")
        }
        loadConnections()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is OnConnectionListInteractionListener) { "parent must implement OnConnectionListInteractionListener" }
        mOnConnectionListInteractionListener = context
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_QUERY, connectionQuery)
        if (mConnectionAdapter != null) {
           // outState.putParcelable(KEY_CONNECTION_STATE, mConnectionAdapter!!.state)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable!!.dispose()
        mConnectionAdapter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connection_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context ?: throw IllegalStateException("context is null")
        super.onViewCreated(view, savedInstanceState)

        connections_list.adapter = mConnectionAdapter
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        connections_list.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        connections_list.addItemDecoration(dividerItemDecoration)
        mConnectionListScrollListener = mConnectionAdapter!!.createOnScrollListener(linearLayoutManager)
        connections_list.addOnScrollListener(mConnectionListScrollListener)
        mViewStateHolder!!.setContentView(connections_list)
        mViewStateHolder!!.setLoadingView(view.findViewById(R.id.loadingIndicator))
        mViewStateHolder!!.setErrorContainer(view.findViewById(R.id.onErrorContainer))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connections_list?.removeOnScrollListener(mConnectionListScrollListener)
    }

    @MainThread
    private fun onConnectionsLoaded(page: Int, connections: List<Connection>) {
        Preconditions.checkNotNull(connections, "connections is null")
        if (page == 0) {
            // loading finished for the first time/page
            mViewStateHolder!!.onSuccess()
        }
        if (mConnectionAdapter != null) {
            mConnectionAdapter!!.setConnections(page, connections.toTypedArray())
        } else {
            if (BuildConfig.DEBUG) Log.w(TAG, "mConnectionAdapter is null", Throwable())
        }
    }

    @AnyThread
    private fun loadConnections() {
        mConnectionAdapter!!.clearConnections()
        loadConnections(connectionQuery, 0)
    }

    @AnyThread
    private fun loadConnections(connectionQuery: ConnectionQuery, page: Int) {
        if (page == 0) {
            mViewStateHolder.onLoading()
        }
        val disposable = Flowable.just(PageQuery(connectionQuery, page))
                .observeOn(Schedulers.io())
                .map { pageQuery: PageQuery ->
                    if (pageQuery.page == 0) {
                        val id = mQueryHistoryDao.addConnection(QueryHistory(pageQuery.query))
                        pageQuery.historyId = id.toInt()
                    }
                    pageQuery
                }
                .map { connectionAPI!!.getConnections(it.query!!) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { page: ConnectionPage -> onConnectionsLoaded(page.pageNumber, page.connections) }
                ) { throwable: Throwable? -> handleError(R.string.error_failed_to_load_connection, throwable) }
        mCompositeDisposable.add(disposable)
    }

    private fun handleError(@StringRes errorMessage: Int, exception: Throwable?) {
        if (BuildConfig.DEBUG) {
            // Log exception
            val errorMessageString = getString(errorMessage)
            Log.e(TAG, errorMessageString, exception)
        }
        if (mViewStateHolder != null) {
            mViewStateHolder!!.onError(errorMessage)
        } else if (exception != null) {
            // Fallback if an error happens outside of view
            startErrorActivity(context!!, exception)
        }
    }

    interface OnConnectionListInteractionListener {
        /**
         * Called when a connection is selected.
         *
         * @param connection the selected connection
         */
        fun onConnectionSelected(connection: Connection)

        /**
         * Called when a list of connection is loaded
         *
         * @param query the query
         */
        fun onQueryStarted(query: ConnectionQuery)
    }

    private class PageQuery(val query: ConnectionQuery?, val page: Int) {
        var historyId = 0
    }

    private inner class OnConnectionSelectedCaller : OnConnectionClickListener {
        override fun onConnectionClicked(connection: Connection?) {
            if (mOnConnectionListInteractionListener != null) {
                mOnConnectionListInteractionListener!!.onConnectionSelected(connection!!)
            }
        }
    }

    companion object {
        private const val ARG_QUERY = "connection_query"
        private const val TAG = "ConnectionListFragment"
        private const val KEY_CONNECTION_STATE = "connection_adapter_state"
        @JvmStatic
        fun newInstance(query: ConnectionQuery?): ConnectionListFragment {
            val connectionListFragment = ConnectionListFragment()
            val arguments = Bundle()
            arguments.putParcelable(ARG_QUERY, query)
            connectionListFragment.arguments = arguments
            return connectionListFragment
        }
    }
}