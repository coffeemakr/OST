package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
import ch.unstable.ost.database.CachedConnectionDAO;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionListFragment extends Fragment {

    private static final int MESSAGE_QUERY_CONNECTION = 1;
    private static final int MESSAGE_ERROR = 2;
    private static final int MESSAGE_CONNECTIONS_LOADED = 3;
    private static final int MESSAGE_CONNECTIONS_LOADING_STARTED = 4;
    private static final int MESSAGE_QUERY_CONNECTION_PAGE = 5;
    private static final int MESSAGE_ON_QUERY = 6;

    private static final String ARG_QUERY = "connection_query";
    private static final String TAG = "ConnectionListFragment";
    private static final String KEY_CONNECTION_STATE = "connection_adapter_state";

    private final OnConnectionSelectedCaller mOnConnectionClickListener = new OnConnectionSelectedCaller();
    private final BackgroundCallback backgroundCallback = new BackgroundCallback();
    private final UICallback uiCallback = new UICallback();
    private final ConnectionAPI connectionAPI;
    private ConnectionListAdapter mConnectionAdapter;
    private ConnectionQuery mQuery;
    private QueryHistory mHistoryEntry;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Handler uiHandler;
    private OnConnectionListInteractionListener mOnConnectionListInteractionListener;
    private View mLoadingIndicator;
    private RecyclerView mConnectionsList;


    private ConnectionListAdapter.Listener mOverScrollListener = new ConnectionListAdapter.Listener() {
        private boolean isLoadablePage(int pageToLoad) {
            return pageToLoad <= connectionAPI.getPageMax() && pageToLoad >= connectionAPI.getPageMin();
        }

        private boolean loadPage(int pageToLoad) {
            if (!isLoadablePage(pageToLoad)) {
                return false;
            }
            Message message = backgroundHandler.obtainMessage(MESSAGE_QUERY_CONNECTION_PAGE);
            message.obj = getConnectionQuery();
            message.arg1 = pageToLoad;
            backgroundHandler.sendMessage(message);
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
    private RecyclerView.OnScrollListener mConnectionListScrollListener;
    private CachedConnectionDAO mCachedConnectionDao;
    private QueryHistoryDao mQueryHistoryDao;

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

        backgroundThread = new HandlerThread("Connections.Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper(), backgroundCallback);

        uiHandler = new Handler(uiCallback);

        CacheDatabase database = Databases.getCacheDatabase(getContext());
        mCachedConnectionDao = database.cachedConnectionDao();
        mQueryHistoryDao = database.queryHistoryDao();

        mConnectionAdapter = new ConnectionListAdapter();
        mConnectionAdapter.setOnLoadMoreListener(mOverScrollListener);
        mConnectionAdapter.setOnConnectionClickListener(mOnConnectionClickListener);
        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getParcelable(ARG_QUERY);
            ConnectionListAdapter.State state = savedInstanceState.getParcelable(KEY_CONNECTION_STATE);
            if (state != null) {
                mConnectionAdapter.restoreState(state);
            } else {
                loadConnectionsAsync(mQuery);
            }
        } else {
            mQuery = getArguments().getParcelable(ARG_QUERY);
            loadConnectionsAsync(mQuery);
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


    private void loadConnectionsAsync(final ConnectionQuery connectionQuery) {
        mConnectionAdapter.clearConnections();
        Message message = backgroundHandler.obtainMessage(MESSAGE_QUERY_CONNECTION, connectionQuery);
        backgroundHandler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectionAdapter = null;
        backgroundThread.quit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mConnectionsList = view.findViewById(R.id.connections_list);
        mConnectionsList.setAdapter(mConnectionAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mConnectionsList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mConnectionsList.addItemDecoration(dividerItemDecoration);
        mConnectionListScrollListener = mConnectionAdapter.createOnScrollListener(linearLayoutManager);
        mConnectionsList.addOnScrollListener(mConnectionListScrollListener);
        mLoadingIndicator = view.findViewById(R.id.loadingIndicator);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mConnectionsList != null) {
            mConnectionsList.removeOnScrollListener(mConnectionListScrollListener);
        }
    }

    /**
     * Get the currently shown connection query
     * @return the query
     */
    public ConnectionQuery getConnectionQuery() {
        return mQuery;
    }


    public interface OnConnectionListInteractionListener {
        /**
         * Called when a connection is selected.
         * @param connection the selected connection
         */
        void onConnectionSelected(@NonNull Connection connection);

        /**
         * Called when a list of connection is loaded
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
        public void onConnectionClicked(Connection connection) {
            if (mOnConnectionListInteractionListener != null) {
                mOnConnectionListInteractionListener.onConnectionSelected(connection);
            }
        }
    }

    private class UICallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CONNECTIONS_LOADING_STARTED:
                    onLoadingStarted();
                    break;
                case MESSAGE_ON_QUERY:
                    if(mOnConnectionListInteractionListener != null) {
                        final ConnectionQuery query = (ConnectionQuery) checkNotNull(msg.obj, "query is null");
                        mOnConnectionListInteractionListener.onQueryStarted(query);
                    } else {
                        if(BuildConfig.DEBUG) Log.w(TAG, "mOnConnectionListInteractionListener is null", new Throwable());
                    }
                    break;
                case MESSAGE_CONNECTIONS_LOADED:
                    if (msg.arg1 == 0) {
                        // loading finished for the first time/page
                        onLoadingFinished();
                    }
                    if (mConnectionAdapter != null) {
                        final Connection[] connections = checkNotNull((Connection[]) msg.obj);
                        mConnectionAdapter.setConnections(msg.arg1, connections);
                    } else {
                        if(BuildConfig.DEBUG) Log.w(TAG, "mConnectionAdapter is null", new Throwable());
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }


        private void onLoadingStarted() {
            mConnectionsList.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        private void onLoadingFinished() {
            mLoadingIndicator.setVisibility(View.GONE);
            mConnectionsList.setVisibility(View.VISIBLE);
        }
    }

    private class BackgroundCallback implements Handler.Callback {


        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_QUERY_CONNECTION:
                    handleConnectionQuery((ConnectionQuery) msg.obj, 0);
                    break;
                case MESSAGE_ERROR:
                    handleError(msg.arg1, (Throwable) msg.obj);
                    break;
                case MESSAGE_QUERY_CONNECTION_PAGE:
                    handleConnectionQuery((ConnectionQuery) msg.obj, msg.arg1);
                    break;
                default:
                    return false;
            }
            return true;
        }

        private void handleError(@StringRes int errorMessage, @Nullable Throwable exception) {
            String errorMessageString = getString(errorMessage);
            Log.e(TAG, errorMessageString, exception);
        }


        private void handleConnectionQuery(final ConnectionQuery connectionQuery, final int page) {
            if (page == 0 && !uiHandler.hasMessages(MESSAGE_CONNECTIONS_LOADING_STARTED)) {
                uiHandler.sendEmptyMessage(MESSAGE_CONNECTIONS_LOADING_STARTED);
            }

            if(page == 0) {
                Message message = Message.obtain(uiHandler);
                message.what = MESSAGE_CONNECTIONS_LOADING_STARTED;
                message.obj = connectionQuery;
                uiHandler.sendMessage(message);
            }

            Disposable disposable = Flowable.just(new PageQuery(connectionQuery, page))
                    .observeOn(Schedulers.io())
                    .map(new Function<PageQuery, PageQuery>() {
                        @Override
                        public PageQuery apply(PageQuery pageQuery) throws Exception {
                            if(pageQuery.page == 0) {
                                long id = mQueryHistoryDao.addConnection(new QueryHistory(pageQuery.query));
                                pageQuery.setHistoryId((int) id);
                            }
                            return pageQuery;
                        }
                    })
                    .map(new Function<PageQuery, PageQuery>() {
                        @Override
                        public PageQuery apply(@NonNull PageQuery pageQuery) throws Exception {
                            Connection[] connections;
                            connections = connectionAPI.getConnections(pageQuery.query, pageQuery.page);
                            for (Connection connection : connections) {
                                Log.d(TAG, connection.toString());
                            }
                            pageQuery.setResult(connections);
                            return pageQuery;
                        }
                    })
                    .subscribe(new Consumer<PageQuery>() {
                        @Override
                        public void accept(PageQuery connections) throws Exception {
                            Message message = uiHandler.obtainMessage(MESSAGE_CONNECTIONS_LOADED, connections.getResult());
                            message.arg1 = connections.page;
                            uiHandler.sendMessage(message);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            handleError(R.string.error_failed_to_load_connection, throwable);
                        }
                    });

        }
    }
}
