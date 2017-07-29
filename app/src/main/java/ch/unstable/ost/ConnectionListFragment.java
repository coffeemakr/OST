package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.ConnectionQuery;

public class ConnectionListFragment extends Fragment {

    private static final String ARG_QUERY = "connection_query";
    private static final String KEY_CONNECTION_LIST = "connection_list";
    private static final String TAG = "ConnectionListFragment";
    private final OnConnectionClickListener mOnConnectionClickListener = new OnConnectionClickListener();
    private final OnNavigationClickListener mOnNavigationButtonClickedListener = new OnNavigationClickListener();
    private final BackgroundCallback backgroundCallback = new BackgroundCallback();
    private final UICallback uiCallback = new UICallback();
    private ConnectionListAdapter mConnectionAdapter;
    private ConnectionQuery mConnectionQuery;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Button mFromButton;
    private Button mToButton;
    private View mReverseDirectionButton;
    private TransportAPI transportAPI;
    private Handler uiHandler;
    private OnConnectionListInteractionListener mOnConnectionListInteractionListener;

    public ConnectionListFragment() {
        // Empty constructor
        transportAPI = new TransportAPI();
    }


    private static void rotateText(Context context, final TextView view, final String text, boolean isFront) {
        int inAnimation;
        int outAnimation;
        if (isFront) {
            inAnimation = R.anim.fade_in_top;
            outAnimation = R.anim.fade_out_top;
        } else {
            inAnimation = R.anim.fade_in_bottom;
            outAnimation = R.anim.fade_out_bottom;
        }
        final Animation fadeIn = AnimationUtils.loadAnimation(context, inAnimation);
        final Animation fadeOut = AnimationUtils.loadAnimation(context, outAnimation);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setText(text);
                view.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeOut);
    }

    public static ConnectionListFragment newInstance(ConnectionQuery query) {
        ConnectionListFragment connectionListFragment = new ConnectionListFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_QUERY, query);
        connectionListFragment.setArguments(arguments);
        return connectionListFragment;
    }

    private void onReverseDirectionRequested() {
        ConnectionQuery query = new ConnectionQuery.Builder(mConnectionQuery)
                .reverseDirection()
                .build();
        updateQuery(query, true, true);
    }

    /**
     * Sets a new query
     *
     * @param query the query
     */
    public void updateQuery(ConnectionQuery query) {
        updateQuery(query, true, false);
    }

    private void updateQuery(ConnectionQuery query, boolean animate, boolean rotate) {
        mConnectionQuery = query;
        loadConnectionsAsync(query);

        if (animate) {
            if (rotate) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.half_rotation);
                mReverseDirectionButton.startAnimation(animation);
            }
            rotateText(getContext(), mFromButton, query.getFrom(), true);
            rotateText(getContext(), mToButton, query.getTo(), false);
        } else {
            mFromButton.setText(query.getFrom());
            mToButton.setText(query.getTo());
        }
    }

    private void onToChangeRequested() {
        // TODO
    }

    private void onFromChangeRequested() {
        // TODO
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
            Connection[] Connections = (Connection[]) savedInstanceState.getParcelableArray(KEY_CONNECTION_LIST);
            if (Connections == null) {
                loadConnectionsAsync(mConnectionQuery);
            } else {
                mConnectionAdapter.setConnections(Connections);
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
        Message message = backgroundHandler.obtainMessage(BackgroundCallback.MESSAGE_QUERY_CONNECTION, connectionQuery);
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
        View view = inflater.inflate(R.layout.fragment_connection_list, container, false);
        RecyclerView connectionsList = (RecyclerView) view.findViewById(R.id.connections_list);
        connectionsList.setAdapter(mConnectionAdapter);
        connectionsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFromButton = (Button) view.findViewById(R.id.fromButton);
        mFromButton.setText(mConnectionQuery.getFrom());
        mFromButton.setOnClickListener(mOnNavigationButtonClickedListener);
        mToButton = (Button) view.findViewById(R.id.toButton);
        mToButton.setText(mConnectionQuery.getTo());
        mToButton.setOnClickListener(mOnNavigationButtonClickedListener);

        mReverseDirectionButton = view.findViewById(R.id.reverseDirectionButton);
        mReverseDirectionButton.setOnClickListener(mOnNavigationButtonClickedListener);
    }

    private class OnConnectionClickListener implements ConnectionListAdapter.OnConnectionClickListener {
        @Override
        public void onConnectionClicked(Connection connection) {
            if(mOnConnectionListInteractionListener != null) {
                mOnConnectionListInteractionListener.onConnectionSelected(connection);
            }
        }
    }

    private class UICallback implements Handler.Callback {
        private static final int MESSAGE_CONNECTIONS_LOADED = 1;

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CONNECTIONS_LOADED:
                    mConnectionAdapter.setConnections((Connection[]) msg.obj);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    private class BackgroundCallback implements Handler.Callback {

        private static final int MESSAGE_QUERY_CONNECTION = 1;
        private static final int MESSAGE_ERROR = 2;

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
            List<Connection> connections;
            try {
                connections = transportAPI.getConnections(connectionQuery);
                for (Connection connection : connections) {
                    Log.d(TAG, connection.toString());
                }
            } catch (IOException e) {
                handleError(R.string.error_failed_to_load_connection, e);
                return;
            }
            Connection[] connectionsArray = connections.toArray(new Connection[connections.size()]);
            Message message = uiHandler.obtainMessage(UICallback.MESSAGE_CONNECTIONS_LOADED, connectionsArray);
            uiHandler.sendMessage(message);
        }
    }

    private class OnNavigationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fromButton:
                    onFromChangeRequested();
                    break;
                case R.id.toButton:
                    onToChangeRequested();
                    break;
                case R.id.reverseDirectionButton:
                    onReverseDirectionRequested();
                    break;
            }
        }
    }

    public interface OnConnectionListInteractionListener {
        void onConnectionSelected(Connection connection);
    }
}
