package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.transport.ConnectionAPI;
import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.database.CachedConnectionDAO;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.model.CachedConnection;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionListFragment extends Fragment {


    private static final int MESSAGE_QUERY_CONNECTION = 1;
    private static final int MESSAGE_ERROR = 2;
    private static final int MESSAGE_CONNECTIONS_LOADED = 3;
    private static final int MESSAGE_CONNECTIONS_LOADING_STARTED = 4;
    private static final int MESSAGE_QUERY_CONNECTION_PAGE = 5;

    private static final String ARG_QUERY = "connection_query";
    private static final String TAG = "ConnectionListFragment";
    private static final String KEY_CONNECTION_STATE = "connection_adapter_state";

    private final OnConnectionSelectedCaller mOnConnectionClickListener = new OnConnectionSelectedCaller();
    private final BackgroundCallback backgroundCallback = new BackgroundCallback();
    private final UICallback uiCallback = new UICallback();
    private final ConnectionAPI connectionAPI;
    private ConnectionListAdapter mConnectionAdapter;
    private ConnectionQuery mConnectionQuery;
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
            if(!isLoadablePage(pageToLoad)) {
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

        mCachedConnectionDao = Databases.getCacheDatabase(getContext()).cachedConnectionDao();


        mConnectionAdapter = new ConnectionListAdapter();
        mConnectionAdapter.setOnLoadMoreListener(mOverScrollListener);
        mConnectionAdapter.setOnConnectionClickListener(mOnConnectionClickListener);
        if (savedInstanceState != null) {
            mConnectionQuery = savedInstanceState.getParcelable(ARG_QUERY);
            ConnectionListAdapter.State state = savedInstanceState.getParcelable(KEY_CONNECTION_STATE);
            if(state != null) {
                mConnectionAdapter.restoreState(state);
            } else {
                loadConnectionsAsync(mConnectionQuery);
            }
        } else {
            mConnectionQuery = getArguments().getParcelable(ARG_QUERY);
            loadConnectionsAsync(mConnectionQuery);
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
        outState.putParcelable(ARG_QUERY, mConnectionQuery);
        if(mConnectionAdapter != null) {
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
        if(mConnectionsList != null) {
            mConnectionsList.removeOnScrollListener(mConnectionListScrollListener);
        }
    }

    public ConnectionQuery getConnectionQuery() {
        return mConnectionQuery;
    }


    public interface OnConnectionListInteractionListener {
        void onConnectionSelected(Connection connection);
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
                case MESSAGE_CONNECTIONS_LOADED:
                    if(msg.arg1 == 0) {
                        // loading finished for the first time/page
                        onLoadingFinished();
                    }
                    if (mConnectionAdapter != null) {
                        mConnectionAdapter.setConnections(msg.arg1, (Connection[]) msg.obj);
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

        private void handleConnectionQuery(ConnectionQuery connectionQuery, int page) {
            if(page == 0 && !uiHandler.hasMessages(MESSAGE_CONNECTIONS_LOADING_STARTED)) {
                uiHandler.sendEmptyMessage(MESSAGE_CONNECTIONS_LOADING_STARTED);
            }
            Connection[] connections;
            try {
                connections = connectionAPI.getConnections(connectionQuery, page);
                for (Connection connection : connections) {
                    Log.d(TAG, connection.toString());
                }
                if(mCachedConnectionDao != null) {
                    if(page == 0) {
                        CachedConnection cachedConnection = new CachedConnection(0, new Date(), connectionQuery, connections);
                        mCachedConnectionDao.addConnection(cachedConnection);
                    }

                }
            } catch (IOException e) {
                handleError(R.string.error_failed_to_load_connection, e);
                return;
            }
            Message message = uiHandler.obtainMessage(MESSAGE_CONNECTIONS_LOADED, connections);
            message.arg1 = page;
            uiHandler.sendMessage(message);
        }
    }
}
