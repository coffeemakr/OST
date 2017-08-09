package ch.unstable.ost;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;

class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.SectionViewHolder> {
    public static final String TAG = "SectionListAdapters";
    private static final int JOURNEY_VIEW_TYPE = 1;
    private static final int WALK_VIEW_TYPE = 2;
    private Section[] sections = new Section[0];
    @Nullable
    private OnSectionClickedListener onJourneyClickedListener;

    private final View.OnClickListener onJourneyItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Section section = (Section) v.getTag();
            if(section == null) {
                if(BuildConfig.DEBUG) Log.w(TAG, "Got tag null for view: " + v);
                return;
            }
            if(onJourneyClickedListener != null) {
                onJourneyClickedListener.onSectionClicked(section);
            }
        }
    };

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case JOURNEY_VIEW_TYPE:
                itemView = inflater.inflate(R.layout.item_connection_section_journey, parent, false);
                return new JourneyViewHolder(itemView);
            case WALK_VIEW_TYPE:
                itemView = inflater.inflate(R.layout.item_connection_section_walk, parent, false);
                return new WalkSectionViewHolder(itemView);
        }
        throw new IllegalStateException("unknown viewType: " + viewType);
    }

    public void onBindJourneyViewHolder(JourneyViewHolder holder, Section section) {
        holder.arrivalStationName.setText(section.getArrivalLocation().getName());
        holder.departureStationName.setText(section.getDepartureLocation().getName());
        holder.arrivalTime.setText(TimeDateUtils.formatTime(section.getArrivalDate()));
        holder.departureTime.setText(TimeDateUtils.formatTime(section.getDepartureDate()));
        holder.productName.setText(section.getLineShortName());
        holder.endDestination.setText(section.getHeadsign());
        holder.departurePlatform.setText(section.getDeparturePlatform());
        holder.arrivalPlatform.setText(section.getArrivalPlatform());
        holder.itemView.setTag(section);
        holder.itemView.setOnClickListener(onJourneyItemClickListener);
    }

    private void onBindWalkViewHolder(WalkSectionViewHolder holder, Section section) {
        holder.departureStationName.setText(section.getDepartureLocation().getName());
        holder.departureTime.setText(TimeDateUtils.formatTime(section.getDepartureDate()));
    }

    @Override
    public int getItemViewType(int position) {
        if (sections[position].isJourney()) {
            return JOURNEY_VIEW_TYPE;
        } else {
            return WALK_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        Section section = sections[position];
        switch (holder.getItemViewType()) {
            case JOURNEY_VIEW_TYPE:
                onBindJourneyViewHolder((JourneyViewHolder) holder, section);
                break;
            case WALK_VIEW_TYPE:
                onBindWalkViewHolder((WalkSectionViewHolder) holder, section);
                break;
        }
    }


    public void setSections(Section[] sections) {
        this.sections = Arrays.copyOf(sections, sections.length);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sections.length;
    }


    public void setOnJourneyClickedListener(@Nullable OnSectionClickedListener onJourneyClickedListener) {
        this.onJourneyClickedListener = onJourneyClickedListener;
    }


    public static class WalkSectionViewHolder extends SectionViewHolder {
        final TextView departureTime;
        final TextView departureStationName;

        public WalkSectionViewHolder(View itemView) {
            super(itemView);
            departureStationName = (TextView) itemView.findViewById(R.id.departureStationName);
            departureTime = (TextView) itemView.findViewById(R.id.departureTime);
        }
    }

    public static class JourneyViewHolder extends SectionViewHolder {
        final TextView productName;
        final TextView endDestination;
        final TextView departurePlatform;
        final TextView arrivalPlatform;
        final TextView arrivalStationName;
        final TextView departureStationName;
        final TextView arrivalTime;
        final TextView departureTime;

        public JourneyViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productName);
            endDestination = (TextView) itemView.findViewById(R.id.endDestination);
            departurePlatform = (TextView) itemView.findViewById(R.id.departurePlatform);
            arrivalPlatform = (TextView) itemView.findViewById(R.id.arrivalPlatform);
            arrivalStationName = (TextView) itemView.findViewById(R.id.arrivalStationName);
            arrivalTime = (TextView) itemView.findViewById(R.id.arrivalTime);
            departureStationName = (TextView) itemView.findViewById(R.id.departureStationName);
            departureTime = (TextView) itemView.findViewById(R.id.departureTime);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public SectionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnSectionClickedListener {
        void onSectionClicked(@NonNull Section section);
    }
}
