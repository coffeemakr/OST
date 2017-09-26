package ch.unstable.ost.views.lists.station;

import android.view.View;

import java.util.Arrays;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.utils.TimeDateUtils;
import ch.unstable.ost.views.lists.SimplerAdapter;
import ch.unstable.ost.views.StopDotView;


public class SectionsStopsListAdapter extends SimplerAdapter<PassingCheckpoint, SectionStationViewHolder> {
    private static final String TAG = "SectionsStopsListAdapter";
    private PassingCheckpoint[] stops;

    public void setStops(PassingCheckpoint[] stops) {
        this.stops = Arrays.copyOf(stops, stops.length);
        notifyDataSetChanged();
    }

    @Override
    public int getLayout(int i) {
        return R.layout.item_connection_journey_station;
    }

    @Override
    public SectionStationViewHolder onCreateViewHolderFromView(View itemView, int i) {
        return new SectionStationViewHolder(itemView);
    }

    @Override
    public void onViewRecycled(SectionStationViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(SectionStationViewHolder holder, int position) {
        PassingCheckpoint stop = stops[position];
        holder.getStationName().setText(stop.getLocation().getName());
        if (position == 0) {
            holder.getStopDotView().setLineMode(StopDotView.Type.TOP);
        } else if (position == stops.length - 1) {
            holder.getStopDotView().setLineMode(StopDotView.Type.BOTTOM);
        } else {
            holder.getStopDotView().setLineMode(StopDotView.Type.BOTH);
        }
        TimeDateUtils.setStationStay(holder.getStationTime(), stop.getArrivalTime(), stop.getDepartureTime());
    }

    @Override
    public int getItemCount() {
        return stops.length;
    }
}
