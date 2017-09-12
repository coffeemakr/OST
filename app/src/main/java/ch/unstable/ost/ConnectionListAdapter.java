package ch.unstable.ost;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;
import ch.unstable.ost.views.ConnectionLineView;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verifyNotNull;


public class ConnectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "ConnectionListAdapter";
    private static final int PROGRESS_TYPE = 0;
    private static final int ITEM_TYPE = 1;
    private static final int MESSAGE_LOAD_MORE_BOTTOM = 0;
    private static final int MESSAGE_LOAD_MORE_TOP = 1;

    private final List<Connection> mConnections = new ArrayList<>();
    private final View.OnClickListener mOnViewHolderClickListener = new OnViewHolderClickListener();
    private final Handler mHandler;
    private OnConnectionClickListener mOnConnectionClickListener;
    private Listener mListener;
    private boolean loadingTop = false;
    private boolean loadingBottom = false;
    private int highestPage = 0;
    private int lowestPage = 0;

    public ConnectionListAdapter() {
        super();
        mHandler = new Handler(new UICallback());
    }

    private static int[] getTravelTimes(Section[] sections) {
        int[] times = new int[sections.length * 2 - 1];
        int i = 0;
        long lastEnd = 0;
        for (Section section : sections) {
            // Walks can be ignored. They are added to the waiting time.
            if (lastEnd != 0) {
                // Waiting time
                times[i] = (int) (section.getDepartureDate().getTime() - lastEnd);
                ++i;
            }
            // Travel time
            lastEnd = section.getArrivalDate().getTime();
            times[i] = (int) (lastEnd - section.getDepartureDate().getTime());
            ++i;
        }
        if (i != times.length) {
            times = Arrays.copyOf(times, i);
        }
        return times;
    }

    private void setLoadingTop(boolean loadingTop) {
        if (this.loadingTop != loadingTop) {
            this.loadingTop = loadingTop;
            if (loadingTop) {
                notifyItemInserted(0);
            } else {
                notifyItemRemoved(0);
            }
        }
    }

    private void setLoadingBottom(boolean loadingBottom) {
        if (this.loadingBottom != loadingBottom) {
            int itemsBefore = getItemCount();
            this.loadingBottom = loadingBottom;
            if (loadingBottom) {
                notifyItemInserted(itemsBefore);
            } else {
                notifyItemRemoved(itemsBefore - 1);
            }
        }
    }

    public RecyclerView.OnScrollListener createOnScrollListener(final LinearLayoutManager linearLayoutManager) {
        final int visibleThreshold = 1;
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mListener == null) return;
                int lastItemPostion = linearLayoutManager.getItemCount() - 1;
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (dy >= 0 && !loadingBottom && lastItemPostion <= (lastVisibleItem + visibleThreshold)) {
                    if(!mHandler.hasMessages(MESSAGE_LOAD_MORE_BOTTOM)) {
                        mHandler.sendEmptyMessage(MESSAGE_LOAD_MORE_BOTTOM);
                    }
                } else if(dy < 0 && !loadingTop && linearLayoutManager.findFirstCompletelyVisibleItemPosition() <= visibleThreshold) {
                    if(!mHandler.hasMessages(MESSAGE_LOAD_MORE_TOP)) {
                        mHandler.sendEmptyMessage(MESSAGE_LOAD_MORE_TOP);
                    }
                }
            }
        };
    }

    @MainThread
    public void setConnections(int page, @NonNull Connection[] connections) {
        if(page == 0) {
            lowestPage = 0;
            highestPage = 0;
            mConnections.clear();
            Collections.addAll(mConnections, connections);
            notifyDataSetChanged();
        } else if(page > 0) {
            appendConnections(connections);
        } else if(page < 0) {
            prependConnections(connections);
        }
    }

    public State getState() {
        Connection[] connections = mConnections.toArray(new Connection[mConnections.size()]);
        return new State(connections, lowestPage, highestPage);
    }

    @MainThread
    public void restoreState(State state) {
        mConnections.clear();
        Collections.addAll(mConnections, state.getConnections());
        loadingTop = false;
        loadingBottom = false;
        lowestPage = state.getLowestPage();
        highestPage = state.getHighestPage();
        notifyDataSetChanged();
    }

    @MainThread
    public void appendConnections(@NonNull Connection[] connections) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(connections, "connections is null");
        setLoadingBottom(false);
        int startPosition = getItemCount();
        int skipFirst = 0;
        for(Connection connection: mConnections) {
            Log.d(TAG, connections[skipFirst] + "  ?= " + connection);
            if(connection.equals(connections[skipFirst])) {
                ++skipFirst;
            } else if(skipFirst > 0) {
                break;
            }
        }
        Log.w(TAG, "Found duplicated items (" + skipFirst + ") -> adding only " + (connections.length - skipFirst));
        for(int i = skipFirst; i < connections.length; ++i) {
            mConnections.add(connections[i]);
        }
        notifyItemRangeInserted(startPosition, connections.length - skipFirst);
        ++highestPage;
    }

    @MainThread
    public void prependConnections(@NonNull Connection[] connections) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(connections, "connections is null");
        setLoadingTop(false);
        ArrayList<Connection> connectionsToInsert = new ArrayList<>(connections.length);
        Collections.addAll(connectionsToInsert, connections);
        mConnections.addAll(0, connectionsToInsert);
        notifyItemRangeInserted(0, connections.length);
        --lowestPage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == ITEM_TYPE) {
            view = inflater.inflate(R.layout.item_connection, parent, false);
            return new ConnectionViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_progress, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == ITEM_TYPE) {
            onBindViewHolder((ConnectionViewHolder) holder, position);
        }
    }

    public void onBindViewHolder(ConnectionViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Connection connection = getItemAt(position);
        Section[] sections = connection.getSections();
        if (sections.length > 0) {
            Section section = sections[0];
            holder.firstEndDestination.setText(formatEndDestination(context, section.getHeadsign()));
            holder.firstTransportName.setText(section.getLineShortName());
            Log.d(TAG, "Binding departure plattform: " + section.getDeparturePlatform());
            holder.platform.setText(formatPlatform(context, section.getDeparturePlatform()));
        } else {
            Log.e(TAG, "No sections");
        }


        String duration = TimeDateUtils.formatDuration(holder.itemView.getResources(),
                connection.getDepartureDate(),
                connection.getArrivalDate());
        holder.duration.setText(duration);
        holder.startTime.setText(TimeDateUtils.formatTime(connection.getDepartureDate()));
        holder.endTime.setText(TimeDateUtils.formatTime(connection.getArrivalDate()));

        int[] times = getTravelTimes(connection.getSections());
        holder.connectionLineView.setLengths(times);

        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(mOnViewHolderClickListener);
    }

    @Nullable
    private String formatPlatform(Context context, @Nullable String platform) {
        if (platform == null) {
            return null;
        } else if (platform.matches("^[0-9]+$")) {
            return context.getString(R.string.format_train_platform, platform);
        } else if (platform.matches("^[A-z]+$")) {
            return context.getString(R.string.format_bus_platform, platform);
        } else {
            return platform;
        }
    }

    @Nullable
    private String formatEndDestination(Context context, @Nullable String endDestination) {
        if (endDestination == null) return null;
        return context.getString(R.string.connection_direction, endDestination);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder instanceof ConnectionViewHolder) {
            holder.itemView.setTag(null);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        int size = mConnections.size();
        if(loadingTop) ++size;
        if(loadingBottom) ++size;
        return size;
    }

    public void clearConnections() {
        mConnections.clear();
        notifyDataSetChanged();
    }

    @MainThread
    public void setOnConnectionClickListener(@Nullable OnConnectionClickListener onConnectionClickListener) {
        this.mOnConnectionClickListener = onConnectionClickListener;
    }

    public void setOnLoadMoreListener(Listener listener) {
        this.mListener = listener;
    }

    @NonNull
    private Connection getItemAt(int position) {
        if (loadingTop) {
            --position;
        }
        return mConnections.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (loadingTop) --position;
        if (position >= 0 && position < mConnections.size()) {
            return ITEM_TYPE;
        } else {
            return PROGRESS_TYPE;
        }
    }

    public int getHighestPage() {
        return highestPage;
    }

    public int getLowestPage() {
        return lowestPage;
    }

    public Connection[] getConnections() {
        return mConnections.toArray(new Connection[mConnections.size()]);
    }

    public interface OnConnectionClickListener {
        void onConnectionClicked(Connection connection);
    }

    public interface Listener {
        boolean onLoadBelow(ConnectionListAdapter adapter, int pageToLoad);

        boolean onLoadAbove(ConnectionListAdapter adapter, int pageToLoad);
    }

    public static class ConnectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView startTime;
        private final TextView endTime;
        private final TextView firstEndDestination;
        private final ConnectionLineView connectionLineView;
        private final TextView firstTransportName;
        private final TextView duration;
        private final TextView platform;

        public ConnectionViewHolder(View itemView) {
            super(checkNotNull(itemView));
            startTime = (TextView) verifyNotNull(itemView.findViewById(R.id.startTime));
            endTime = (TextView) verifyNotNull(itemView.findViewById(R.id.endTime));
            firstEndDestination = (TextView) verifyNotNull(itemView.findViewById(R.id.firstSectionEndDestination));
            connectionLineView = (ConnectionLineView) verifyNotNull(itemView.findViewById(R.id.connectionLineView));
            firstTransportName = (TextView) verifyNotNull(itemView.findViewById(R.id.firstTransportName));
            duration = (TextView) verifyNotNull(itemView.findViewById(R.id.duration));
            platform = (TextView) verifyNotNull(itemView.findViewById(R.id.platform));
        }
    }

    public class OnViewHolderClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ConnectionViewHolder connectionViewHolder = (ConnectionViewHolder) v.getTag();
            int position = connectionViewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            Connection connection;
            try {
                connection = getItemAt(position);
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to get connection", e);
                return;
            }
            if (mOnConnectionClickListener != null) {
                mOnConnectionClickListener.onConnectionClicked(connection);
            }
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private final View progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            this.progressBar = verifyNotNull(itemView.findViewById(R.id.progressBar));
        }
    }

    private class UICallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOAD_MORE_BOTTOM:
                    if(mListener.onLoadBelow(ConnectionListAdapter.this, getHighestPage() + 1)) {
                        setLoadingBottom(true);
                    } else {
                        setLoadingBottom(false);
                    }
                    return true;
                case MESSAGE_LOAD_MORE_TOP:
                    if(mListener.onLoadAbove(ConnectionListAdapter.this, getLowestPage() - 1)) {
                        setLoadingTop(true);
                    } else {
                        setLoadingTop(false);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    public static class State implements Parcelable {
        private final Connection[] connections;
        private final int lowestPage;
        private final int highestPage;

        public State(Connection[] connections, int lowestPage, int highestPage) {
            this.connections = connections;
            this.lowestPage = lowestPage;
            this.highestPage = highestPage;
        }

        private State(Parcel in) {
            connections = in.createTypedArray(Connection.CREATOR);
            lowestPage = in.readInt();
            highestPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(connections, flags);
            dest.writeInt(lowestPage);
            dest.writeInt(highestPage);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };

        public Connection[] getConnections() {
            return connections;
        }

        public int getLowestPage() {
            return lowestPage;
        }

        public int getHighestPage() {
            return highestPage;
        }
    }
}
