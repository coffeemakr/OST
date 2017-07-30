package ch.unstable.ost;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import ch.unstable.ost.api.transport.model.Checkpoint;
import ch.unstable.ost.api.transport.model.Journey;
import ch.unstable.ost.api.transport.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;

class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.SectionViewHolder> {

    private static final int JOURNEY_VIEW_TYPE = 1;
    private static final int WALK_VIEW_TYPE = 2;
    private Section[] sections = new Section[0];

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
        Journey journey = section.getJourney();
        Checkpoint arrival = section.getArrival();
        Checkpoint departure = section.getDeparture();
        holder.arrivalStationName.setText(arrival.getStation().getName());
        holder.departureStationName.setText(departure.getStation().getName());
        holder.arrivalTime.setText(TimeDateUtils.formatTime(arrival.getArrival()));
        holder.departureTime.setText(TimeDateUtils.formatTime(departure.getDepartureTime()));
        holder.productName.setText(journey.getName());
        System.out.println("Journey: " + journey);
        holder.endDestination.setText(journey.getTo());
        holder.departurePlatform.setText(section.getDeparture().getPlatform());
        holder.arrivalPlatform.setText(section.getArrival().getPlatform());
    }

    private void onBindWalkViewHolder(WalkSectionViewHolder holder, Section section) {
        holder.departureStationName.setText(section.getDeparture().getStation().getName());
        holder.departureTime.setText(TimeDateUtils.formatTime(section.getDeparture().getDepartureTime()));
    }

    @Override
    public int getItemViewType(int position) {
        if(sections[position].isJourney()) {
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
}