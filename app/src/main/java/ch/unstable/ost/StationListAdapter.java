package ch.unstable.ost;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.theme.ThemeHelper;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.ViewHolder> {

    private final Handler mHandler;
    private final
    @DrawableRes
    int trainIcon;
    private final
    @DrawableRes
    int busIcon;
    private Location[] mLocations = new Location[0];
    @Nullable
    private OnStationClickListener mOnStationClickListener;
    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final OnStationClickListener listener = mOnStationClickListener;
            if (listener != null) {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                if (position != NO_POSITION) {
                    final Location location = mLocations[position];
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onStationClicked(location);
                        }
                    });
                }
            }
        }
    };
    @MainThread
    public StationListAdapter(Context context) {
        mHandler = new Handler();
        trainIcon = ThemeHelper.getThemedDrawable(context, R.attr.ic_direction_railway_24dp);
        busIcon = ThemeHelper.getThemedDrawable(context, R.attr.ic_directions_bus_24dp);

    }

    @Override
    public StationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_station, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StationListAdapter.ViewHolder holder, int position) {
        Location location = mLocations[position];
        holder.stationName.setText(location.getName());
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(mOnItemClickListener);
        switch (location.getType()) {
            case TRAIN:
                holder.transportationIcon.setImageResource(trainIcon);
                break;
            case BUS:
                holder.transportationIcon.setImageResource(busIcon);
                break;
            case POI:
            case ADDRESS:
            case UNKNOWN:
            default:
                holder.transportationIcon.setImageDrawable(null);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setTag(null);
        holder.itemView.setOnClickListener(null);
        holder.itemView.setClickable(true);
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

    public interface OnStationClickListener {
        void onStationClicked(Location location);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView stationName;
        private final ImageView transportationIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            stationName = (TextView) itemView.findViewById(R.id.stationName);
            transportationIcon = (ImageView) itemView.findViewById(R.id.transportationIcon);
        }
    }
}
