package ch.unstable.ost;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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


public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.ConnectionViewHolder> {
    public static final String TAG = "ConnectionListAdapter";
    private final List<Connection> mConnections = new ArrayList<>();
    private final View.OnClickListener mOnViewHolderClickListener = new OnViewHolderClickListener();
    private OnConnectionClickListener mOnConnectionClickListener;

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

    public void setConnections(@NonNull Connection... connections) {
        mConnections.clear();
        Collections.addAll(mConnections, connections);
        notifyDataSetChanged();
    }

    @Override
    public ConnectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_connection, parent, false);
        return new ConnectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConnectionViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Connection connection = mConnections.get(position);
        Section[] sections = connection.getSections();
        if (sections.length > 0) {
            Section section = sections[0];
            holder.firstEndDestination.setText(formatEndDestination(context, section.getHeadsign()));
            holder.firstTransportName.setText(section.getLineShortName());
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
        if (platform == null) return null;
        if (platform.matches("^[0-9]+$")) {
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
    public void onViewRecycled(ConnectionViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setTag(null);
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    public void clearConnections() {
        mConnections.clear();
        notifyDataSetChanged();
    }

    @MainThread
    public void setOnConnectionClickListener(@Nullable OnConnectionClickListener onConnectionClickListener) {
        this.mOnConnectionClickListener = onConnectionClickListener;
    }


    public interface OnConnectionClickListener {
        void onConnectionClicked(Connection connection);
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
            super(itemView);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            firstEndDestination = (TextView) itemView.findViewById(R.id.firstSectionEndDestination);
            connectionLineView = (ConnectionLineView) itemView.findViewById(R.id.connectionLineView);
            firstTransportName = (TextView) itemView.findViewById(R.id.firstTransportName);
            duration = (TextView) itemView.findViewById(R.id.duration);
            platform = (TextView) itemView.findViewById(R.id.platform);
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
                connection = mConnections.get(position);
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to get connection", e);
                return;
            }
            if (mOnConnectionClickListener != null) {
                mOnConnectionClickListener.onConnectionClicked(connection);
            }
        }
    }
}
