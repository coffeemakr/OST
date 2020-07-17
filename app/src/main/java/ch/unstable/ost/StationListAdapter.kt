package ch.unstable.ost

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.api.model.Location
import ch.unstable.ost.api.model.Location.StationType
import ch.unstable.ost.theme.ThemeHelper
import java.util.*

internal class StationListAdapter @MainThread constructor(context: Context?) : RecyclerView.Adapter<StationListAdapter.ViewHolder>() {
    private val mHandler: Handler = Handler()

    @DrawableRes
    private val trainIcon: Int = ThemeHelper.getThemedDrawable(context, R.attr.ic_direction_railway_24dp)

    @DrawableRes
    private val busIcon: Int = ThemeHelper.getThemedDrawable(context, R.attr.ic_directions_bus_24dp)

    @DrawableRes
    private val tramIcon: Int = ThemeHelper.getThemedDrawable(context, R.attr.ic_direction_tram_24dp)

    private var mLocations = arrayOfNulls<Location>(0)
    private var mOnStationClickListener: OnStationClickListener? = null
    private val mOnItemClickListener = View.OnClickListener { v ->
        val listener = mOnStationClickListener
        if (listener != null) {
            val viewHolder = v.tag as ViewHolder
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val location = mLocations[position]
                mHandler.post { listener.onStationClicked(location) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_station, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = mLocations[position]
        holder.stationName.text = location!!.name
        holder.itemView.tag = holder
        holder.itemView.setOnClickListener(mOnItemClickListener)
        when (location.type) {
            StationType.TRAIN -> holder.transportationIcon.setImageResource(trainIcon)
            StationType.BUS -> holder.transportationIcon.setImageResource(busIcon)
            StationType.TRAM -> holder.transportationIcon.setImageResource(tramIcon)
            StationType.POI, StationType.ADDRESS, StationType.UNKNOWN -> holder.transportationIcon.setImageDrawable(null)
            else -> holder.transportationIcon.setImageDrawable(null)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.tag = null
        holder.itemView.setOnClickListener(null)
        holder.itemView.isClickable = true
    }

    @MainThread
    fun setLocations(locations: Array<Location?>) {
        mLocations = locations.copyOf(locations.size)
        notifyDataSetChanged()
    }

    fun setOnStationClickListener(onStationClickListener: OnStationClickListener?) {
        mOnStationClickListener = onStationClickListener
    }

    override fun getItemCount(): Int {
        return mLocations.size
    }

    interface OnStationClickListener {
        fun onStationClicked(location: Location?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationName: TextView = itemView.findViewById(R.id.stationName)
        val transportationIcon: ImageView = itemView.findViewById(R.id.transportationIcon)
    }

}