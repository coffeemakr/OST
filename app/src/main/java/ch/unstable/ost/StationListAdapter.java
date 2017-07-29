package ch.unstable.ost;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.unstable.ost.api.transport.model.Location;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.ViewHolder> {

    private Location[] mLocations = new Location[0];
    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnStationClickListener != null) {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                if(position != NO_POSITION) {
                    Location location = mLocations[position];
                    mOnStationClickListener.onStationClicked(location);
                }
            }
        }
    };
    @Nullable
    private OnStationClickListener mOnStationClickListener;

    @Override
    public StationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_station, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StationListAdapter.ViewHolder holder, int position) {
        Location location = mLocations[position];
        holder.stationName.setText(location.getName());
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(mOnItemClickListener);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setTag(null);
        holder.itemView.setOnClickListener(null);
    }

    @MainThread
    public void setLocations(Location[] locations) {
        mLocations = locations;
        notifyDataSetChanged();
    }

    public void setOnStationClickListener(@Nullable OnStationClickListener onStationClickListener) {
        this.mOnStationClickListener = onStationClickListener;
    }

    @Override
    public int getItemCount() {
        return mLocations.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView stationName;

        public ViewHolder(View itemView) {
            super(itemView);
            stationName = (TextView) itemView.findViewById(R.id.stationName);
        }
    }

    public interface OnStationClickListener {
        void onStationClicked(Location location);
    }
}
