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
        startTime = verifyNotNull(itemView.findViewById(R.id.startTime));
        endTime = verifyNotNull(itemView.findViewById(R.id.endTime));
        firstEndDestination = verifyNotNull(itemView.findViewById(R.id.firstSectionEndDestination));
        connectionLineView = verifyNotNull(itemView.findViewById(R.id.connectionLineView));
        firstTransportName = verifyNotNull(itemView.findViewById(R.id.firstTransportName));
        duration = verifyNotNull(itemView.findViewById(R.id.duration));
        platform = verifyNotNull(itemView.findViewById(R.id.platform));
    }
}
