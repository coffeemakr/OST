package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.transport.ConnectionAPI;
import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.database.CacheDatabase;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import ch.unstable.ost.views.lists.connection.ConnectionListAdapter;
import ch.unstable.ost.views.NoAnimationStrategy;
import ch.unstable.ost.views.ViewStateHolder;
import ch.unstable.ost.views.lists.connection.ConnectionListAdapterState;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static ch.unstable.ost.utils.NavHelperKt.startErrorActivity;
import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionListFragment extends Fragment {


    private static final String ARG_QUERY = "connection_query";
    private static final String TAG = "ConnectionListFragment";
    private static final String KEY_CONNECTION_STATE = "connection_adapter_state";

    private final OnConnectionSelectedCaller mOnConnectionClickListener = new OnConnectionSelectedCaller();
    private final ConnectionAPI connectionAPI;
    private ConnectionListAdapter mConnectionAdapter;
    private ConnectionQuery mQuery;
    private OnConnectionListInteractionListener mOnConnectionListInteractionListener;
    private RecyclerView mConnectionList;
    private RecyclerView.OnScrollListener mConnectionListScrollListener;
    private QueryHistoryDao mQueryHistoryDao;
    private ViewStateHolder mViewStateHolder;
    private CompositeDisposable mCompositeDisposable;
    private final ConnectionListAdapter.Listener mOverScrollListener = new ConnectionListAdapter.Listener() {
        private boolean isLoadablePage(int pageToLoad) {
            return pageToLoad <= connectionAPI.getPageMax() && pageToLoad >= connectionAPI.getPageMin();
        }

        private boolean loadPage(int pageToLoad) {
            if (!isLoadablePage(pageToLoad)) {
                return false;
            }
            loadConnections(getConnectionQuery(), pageToLoad);
            return true;
        }

        @Override
        public boolean onLoadBelow(ConnectionListAdapter adapter, int pageToLoad) {
            return loadPage(pageToLoad);
        }

        @Override
        public boolean onLoadAbove(ConnectionListAdapter adapter, int pageToLoad) {
            return loadPage(pageToLoad);
        }
    };

    public ConnectionListFragment() {
        // Empty constructor
        connectionAPI = new TransportAPI();
    }


    public static ConnectionListFragment newInstance(ConnectionQuery query) {
        ConnectionListFragment connectionListFragment = new ConnectionListFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_QUERY, query);
        connectionListFragment.setArguments(arguments);
        return connectionListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();
        CacheDatabase database = Databases.getCacheDatabase(getContext());
        mQueryHistoryDao = database.queryHistoryDao();

        mViewStateHolder = new ViewStateHolder(new NoAnimationStrategy());
        mViewStateHolder.setOnRetryClickListener(view -> loadConnections());
        mConnectionAdapter = new ConnectionListAdapter();
        mConnectionAdapter.setOnLoadMoreListener(mOverScrollListener);
        mConnectionAdapter.setOnConnectionClickListener(mOnConnectionClickListener);
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getParcelable(ARG_QUERY);
            ConnectionListAdapterState state = savedInstanceState.getParcelable(KEY_CONNECTION_STATE);
            if (state != null) {
                mConnectionAdapter.restoreState(state);
            } else {
                loadConnections();
            }
        } else {
            mQuery = getArguments().getParcelable(ARG_QUERY);
            loadConnections();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnConnectionListInteractionListener)) {
            throw new IllegalStateException("parent must implement OnConnectionListInteractionListener");
        }
        mOnConnectionListInteractionListener = (OnConnectionListInteractionListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_QUERY, mQuery);
        if (mConnectionAdapter != null) {
            outState.putParcelable(KEY_CONNECTION_STATE, mConnectionAdapter.getState());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mConnectionAdapter = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mConnectionList = view.findViewById(R.id.connections_list);
        mConnectionList.setAdapter(mConnectionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mConnectionList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mConnectionList.addItemDecoration(dividerItemDecoration);
        mConnectionListScrollListener = mConnectionAdapter.createOnScrollListener(linearLayoutManager);
        mConnectionList.addOnScrollListener(mConnectionListScrollListener);

        mViewStateHolder.setContentView(mConnectionList);
        mViewStateHolder.setLoadingView(view.findViewById(R.id.loadingIndicator));
        mViewStateHolder.setErrorContainer(view.findViewById(R.id.onErrorContainer));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mConnectionList != null) {
            mConnectionList.removeOnScrollListener(mConnectionListScrollListener);
        }
    }

    /**
     * Get the currently shown connection query
     *
     * @return the query
     */
    public ConnectionQuery getConnectionQuery() {
        return mQuery;
    }

    @MainThread
    private void onConnectionsLoaded(int page, Connection[] connections) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(connections, "connections is null");
        if (page == 0) {
            // loading finished for the first time/page
            mViewStateHolder.onSuccess();
        }
        if (mConnectionAdapter != null) {
            mConnectionAdapter.setConnections(page, connections);
        } else {
            if (BuildConfig.DEBUG) Log.w(TAG, "mConnectionAdapter is null", new Throwable());
        }
    }

    @AnyThread
    private void loadConnections() {
        mConnectionAdapter.clearConnections();
        loadConnections(getConnectionQuery(), 0);
    }

    @AnyThread
    private void loadConnections(final ConnectionQuery connectionQuery, final int page) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(connectionQuery, "connectionQuery");
        if (page == 0) {
            mViewStateHolder.onLoading();
        }

        Disposable disposable = Flowable.just(new PageQuery(connectionQuery, page))
                .observeOn(Schedulers.io())
                .map(pageQuery -> {
                    if (pageQuery.page == 0) {
                        long id = mQueryHistoryDao.addConnection(new QueryHistory(pageQuery.query));
                        pageQuery.setHistoryId((int) id);
                    }
                    return pageQuery;
                })
                .map(pageQuery -> {
                    Connection[] connections;
                    connections = connectionAPI.getConnections(pageQuery.query, pageQuery.page);
                    pageQuery.setResult(connections);
                    return pageQuery;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        connections -> onConnectionsLoaded(connections.page, connections.getResult()),
                        throwable -> handleError(R.string.error_failed_to_load_connection, throwable));
        mCompositeDisposable.add(disposable);
    }

    private void handleError(@StringRes int errorMessage, @NonNull final Throwable exception) {
        if (BuildConfig.DEBUG) {
            // Log exception
            String errorMessageString = getString(errorMessage);
            Log.e(TAG, errorMessageString, exception);
        }
        if (mViewStateHolder != null) {
            mViewStateHolder.onError(errorMessage);
        } else {
            // Fallback if an error happens outside of view
            startErrorActivity(getContext(), exception);
        }
    }


    public interface OnConnectionListInteractionListener {
        /**
         * Called when a connection is selected.
         *
         * @param connection the selected connection
         */
        void onConnectionSelected(@NonNull Connection connection);

        /**
         * Called when a list of connection is loaded
         *
         * @param query the query
         */
        void onQueryStarted(@NonNull ConnectionQuery query);
    }

    private static class PageQuery {
        public final ConnectionQuery query;
        public final int page;
        private int historyId = 0;
        private Connection[] result;

        public PageQuery(ConnectionQuery query, int page) {
            this.query = query;
            this.page = page;
        }

        public int getHistoryId() {
            return historyId;
        }

        public void setHistoryId(int historyId) {
            this.historyId = historyId;
        }

        public Connection[] getResult() {
            return result;
        }

        public void setResult(Connection[] result) {
            this.result = result;
        }
    }

    private class OnConnectionSelectedCaller implements ConnectionListAdapter.OnConnectionClickListener {
        @Override
        public void onConnectionClicked(@NonNull Connection connection) {
            if (mOnConnectionListInteractionListener != null) {
                mOnConnectionListInteractionListener.onConnectionSelected(connection);
            }
        }
    }
}
