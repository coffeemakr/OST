package ch.unstable.ost.views.lists.station;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.unstable.ost.R;
import ch.unstable.ost.views.StopDotView;

public class SectionStationViewHolder extends RecyclerView.ViewHolder {

    public final TextView stationName;
    public final TextView stationTime;
    public final StopDotView stopDotView;

    public SectionStationViewHolder(View itemView) {
        super(itemView);
        stationName = itemView.findViewById(R.id.stationName);
        stationTime = itemView.findViewById(R.id.stationTime);
        stopDotView = itemView.findViewById(R.id.stopDotView);
    }
}
