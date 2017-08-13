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

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.transport.TransportAPI;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionListFragment extends Fragment {


    private static final int MESSAGE_QUERY_CONNECTION = 1;
    private static final int MESSAGE_ERROR = 2;
    private static final int MESSAGE_CONNECTIONS_LOADED = 3;
    private static final int MESSAGE_CONNECTIONS_LOADING_STARTED = 4;

    private static final String ARG_QUERY = "connection_query";
    private static final String KEY_CONNECTION_LIST = "connection_list";
    private static final String TAG = "ConnectionListFragment";
    private final OnConnectionSelectedCaller mOnConnectionClickListener = new OnConnectionSelectedCaller();
    private final BackgroundCallback backgroundCallback = new BackgroundCallback();
    private final UICallback uiCallback = new UICallback();
    private final TransportAPI transportAPI;
    private ConnectionListAdapter mConnectionAdapter;
    private ConnectionQuery mConnectionQuery;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Handler uiHandler;
    private OnConnectionListInteractionListener mOnConnectionListInteractionListener;
    private View mLoadingIndicator;
    private RecyclerView mConnectionsList;
    private OverscrollListener.Listener mOverScrollListener = new OverscrollListener.Listener() {
        @Override
        public void onScrolledBelow(RecyclerView recyclerView) {
            Log.d(TAG, "on scrolled below");
        }

        @Override
        public void onScrolledAbove(RecyclerView recyclerView) {
            Log.d(TAG, "on scrolled above");
        }
    };
    private OverscrollListener mConnectionListScrollListener;

    public ConnectionListFragment() {
        // Empty constructor
        transportAPI = new TransportAPI();
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

        mConnectionAdapter = new ConnectionListAdapter();

        mConnectionAdapter.setOnConnectionClickListener(mOnConnectionClickListener);
        if (savedInstanceState != null) {
            mConnectionQuery = savedInstanceState.getParcelable(ARG_QUERY);
            Connection[] connections = (Connection[]) savedInstanceState.getParcelableArray(KEY_CONNECTION_LIST);
            if (connections == null) {
                loadConnectionsAsync(mConnectionQuery);
            } else {
                mConnectionAdapter.setConnections(connections);
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
        mConnectionsList = (RecyclerView) view.findViewById(R.id.connections_list);
        mConnectionsList.setAdapter(mConnectionAdapter);
        mConnectionsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mConnectionsList.addItemDecoration(dividerItemDecoration);
        mConnectionListScrollListener = new OverscrollListener(mOverScrollListener);
        mConnectionsList.addOnScrollListener(mConnectionListScrollListener);
        mLoadingIndicator = view.findViewById(R.id.loadingIndicator);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mConnectionsList.removeOnScrollListener(mConnectionListScrollListener);
        mConnectionListScrollListener = null;
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
                    onLoadingFinished();
                    if (mConnectionAdapter != null) {
                        mConnectionAdapter.setConnections((Connection[]) msg.obj);
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
                    handleConnectionQuery((ConnectionQuery) msg.obj);
                    break;
                case MESSAGE_ERROR:
                    handleError(msg.arg1, (Throwable) msg.obj);
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

        private void handleConnectionQuery(ConnectionQuery connectionQuery) {
            uiHandler.sendEmptyMessage(MESSAGE_CONNECTIONS_LOADING_STARTED);
            Connection[] connections;
            try {
                connections = transportAPI.getConnections(connectionQuery, 0);
                for (Connection connection : connections) {
                    Log.d(TAG, connection.toString());
                }
            } catch (IOException e) {
                handleError(R.string.error_failed_to_load_connection, e);
                return;
            }
            Message message = uiHandler.obtainMessage(MESSAGE_CONNECTIONS_LOADED, connections);
            uiHandler.sendMessage(message);
        }
    }

    private static class OverscrollListener extends RecyclerView.OnScrollListener {

        private Listener mListener;

        public OverscrollListener(Listener listener) {
            this.mListener = checkNotNull(listener, "listener is null");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(mListener == null) return;
            Log.d(TAG, "onScroll: " + dx + " " + dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if(layoutManager.getItemCount() == layoutManager.findFirstCompletelyVisibleItemPosition() && dy > 0) {
                mListener.onScrolledBelow(recyclerView);
            } else if(0 == layoutManager.findFirstCompletelyVisibleItemPosition() && dy < 0) {
                mListener.onScrolledAbove(recyclerView);
            }
        }

        public interface Listener {
            void onScrolledBelow(RecyclerView recyclerView);
            void onScrolledAbove(RecyclerView recyclerView);
        }
    }
}
