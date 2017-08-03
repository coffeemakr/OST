package ch.unstable.ost;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import ch.unstable.ost.api.transport.model.Checkpoint;
import ch.unstable.ost.api.transport.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;
import ch.unstable.ost.views.StopDotView;

public class SectionDetailFragment extends Fragment {
    private static final String KEY_SECTION = "SectionDetailFragment.KEY_SECTION";
    private static final String TAG = SectionDetailFragment.class.getSimpleName();
    private Section mSection;
    private StopsListAdapter mStopsListAdapter;

    public static SectionDetailFragment newInstance(Section section) {
        if (!section.isJourney()) {
            throw new IllegalArgumentException("section must me a journey");
        }
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_SECTION, section);
        SectionDetailFragment fragment = new SectionDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSection = savedInstanceState.getParcelable(KEY_SECTION);
        } else {
            mSection = getArguments().getParcelable(KEY_SECTION);
        }
        if (mSection == null) {
            throw new IllegalStateException("section not set");
        }
        mStopsListAdapter = new StopsListAdapter();
        mStopsListAdapter.setStops(mSection.getJourney().getPassList());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SECTION, mSection);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journey_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView stationsList = (RecyclerView) view.findViewById(R.id.stationsList);
        stationsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        stationsList.setAdapter(mStopsListAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView stationName;
        private final TextView departureTime;
        private final StopDotView stopDotView;

        public ViewHolder(View itemView) {
            super(itemView);
            stationName = (TextView) itemView.findViewById(R.id.stationName);
            departureTime = (TextView) itemView.findViewById(R.id.departureTime);
            stopDotView = (StopDotView) itemView.findViewById(R.id.stopDotView);
        }
    }

    private class StopsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Checkpoint stops[];

        public void setStops(Checkpoint[] stops) {
            this.stops = stops;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_connection_journey_station, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
            holder.stopDotView.setLineMode(StopDotView.Type.BOTH);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Checkpoint stop = stops[position];
            holder.stationName.setText(stop.getStation().getName());
            if(position == 0) {
                holder.stopDotView.setLineMode(StopDotView.Type.TOP);
            } else if(position == stops.length - 1) {
                holder.stopDotView.setLineMode(StopDotView.Type.BOTTOM);
            }
            String time = null;
            Date shownDate = stop.getDepartureTime();
            if(shownDate == null) {
                shownDate = stop.getArrival();
            }
            if(shownDate != null) {
                try {
                    time = TimeDateUtils.formatTime(shownDate);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Failed to get arrival date", e);
                }
            }
            holder.departureTime.setText(time);
        }

        @Override
        public int getItemCount() {
            return stops.length;
        }
    }
}
