package ch.unstable.ost.lists.connection;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.unstable.ost.R;
import ch.unstable.ost.views.ConnectionLineView;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verifyNotNull;

/**
 * View holder for connection items
 */
public class ConnectionViewHolder extends RecyclerView.ViewHolder {
    public final TextView startTime;
    public final TextView endTime;
    public final TextView firstEndDestination;
    public final ConnectionLineView connectionLineView;
    public final TextView firstTransportName;
    public final TextView duration;
    public final TextView platform;

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
