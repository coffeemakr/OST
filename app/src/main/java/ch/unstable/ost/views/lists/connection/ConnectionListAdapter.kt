package ch.unstable.ost.views.lists.connection

import android.os.Handler
import android.os.Message
import android.support.annotation.MainThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import com.google.common.base.Preconditions.checkNotNull
import java.util.*


const private val TAG = "ConnectionListAdapter"
const private val PROGRESS_TYPE = 0
const private val ITEM_TYPE = 1
const private val MESSAGE_LOAD_MORE_BOTTOM = 0
const private val MESSAGE_LOAD_MORE_TOP = 1

class ConnectionListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val connections = ArrayList<Connection>()
    private val mOnViewHolderClickListener = OnViewHolderClickListener()
    private val mHandler: Handler = Handler(UICallback(this))
    private var mOnConnectionClickListener: OnConnectionClickListener? = null

    var onLoadMoreListener: Listener? = null

    private var loadingTop = false
        private set(value) {
            if (field != value) {
                field = value
                if (value) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }

    private var loadingBottom = false
        private set(value) {
            if (field != value) {
                val itemsBefore = itemCount
                field = value
                if (value) {
                    notifyItemInserted(itemsBefore)
                } else {
                    notifyItemRemoved(itemsBefore - 1)
                }
            }
        }

    var highestPage = 0
        private set

    var lowestPage = 0
        private set

    val state: ConnectionListAdapterState
        get() {
            return ConnectionListAdapterState(connections.toTypedArray(), lowestPage, highestPage)
        }

    @JvmOverloads
    fun createOnScrollListener(linearLayoutManager: LinearLayoutManager, visibleThreshold: Int = 1): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (onLoadMoreListener == null) return
                val lastItemPosition = linearLayoutManager.itemCount - 1
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (dy >= 0 && !loadingBottom && lastItemPosition <= lastVisibleItem + visibleThreshold) {
                    if (!mHandler.hasMessages(MESSAGE_LOAD_MORE_BOTTOM)) {
                        mHandler.sendEmptyMessage(MESSAGE_LOAD_MORE_BOTTOM)
                    }
                } else if (dy < 0 && !loadingTop && linearLayoutManager.findFirstCompletelyVisibleItemPosition() <= visibleThreshold) {
                    if (!mHandler.hasMessages(MESSAGE_LOAD_MORE_TOP)) {
                        mHandler.sendEmptyMessage(MESSAGE_LOAD_MORE_TOP)
                    }
                }
            }
        }
    }

    @MainThread
    fun setConnections(page: Int, connections: Array<Connection>) {
        if (page == 0) {
            lowestPage = 0
            highestPage = 0
            this.connections.clear()
            Collections.addAll(this.connections, *connections)
            notifyDataSetChanged()
        } else if (page > 0) {
            appendConnections(connections)
        } else if (page < 0) {
            prependConnections(connections)
        }
    }

    @MainThread
    fun restoreState(state: ConnectionListAdapterState) {
        connections.clear()
        connections.addAll(state.connections)
        loadingTop = false
        loadingBottom = false
        lowestPage = state.lowestPage
        highestPage = state.highestPage
        notifyDataSetChanged()
    }

    @MainThread
    fun appendConnections(connections: Array<Connection>) {

        checkNotNull(connections, "connections is null")
        loadingBottom = false
        val startPosition = itemCount
        var skipFirst = 0
        for (connection in this.connections) {
            Log.d(TAG, connections[skipFirst].toString() + "  ?= " + connection)
            if (connection == connections[skipFirst]) {
                ++skipFirst
            } else if (skipFirst > 0) {
                break
            }
        }
        Log.w(TAG, "Found duplicated items (" + skipFirst + ") -> adding only " + (connections.size - skipFirst))
        for (i in skipFirst until connections.size) {
            this.connections.add(connections[i])
        }
        notifyItemRangeInserted(startPosition, connections.size - skipFirst)
        ++highestPage
    }

    @MainThread
    fun prependConnections(connections: Array<Connection>) {
        checkNotNull(connections, "connections is null")
        loadingTop = false
        val connectionsToInsert = ArrayList<Connection>(connections.size)
        Collections.addAll(connectionsToInsert, *connections)
        this.connections.addAll(0, connectionsToInsert)
        notifyItemRangeInserted(0, connections.size)
        --lowestPage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == ITEM_TYPE) {
            view = inflater.inflate(R.layout.item_connection, parent, false)
            return ConnectionViewHolder(view)
        } else {
            view = inflater.inflate(R.layout.item_progress, parent, false)
            return ProgressViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE) {
            onBindViewHolder(holder as ConnectionViewHolder, position)
        }
    }

    fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val connection = getItemAt(position)
        bindConnection(connection, holder)
        holder.itemView.tag = holder
        holder.itemView.setOnClickListener(mOnViewHolderClickListener)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        super.onViewRecycled(holder)
        if (holder?.itemViewType == ITEM_TYPE) {
            holder.itemView.tag = null
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        val size = connections.size
        return when {
            loadingTop && loadingBottom -> size + 2
            loadingTop || loadingBottom -> size + 1
            else -> size
        }
    }

    fun clearConnections() {
        connections.clear()
        notifyDataSetChanged()
    }

    @MainThread
    fun setOnConnectionClickListener(onConnectionClickListener: OnConnectionClickListener?) {
        this.mOnConnectionClickListener = onConnectionClickListener
    }

    private fun getArrayPosition(itemPosition: Int): Int {
        return if (loadingTop) itemPosition - 1 else itemPosition
    }

    private fun getItemAt(itemPosition: Int): Connection {
        return connections[getArrayPosition(itemPosition)]
    }

    override fun getItemViewType(itemPosition: Int): Int {
        val arrayPosition = getArrayPosition(itemPosition)
        return if (arrayPosition in 0 until connections.size) {
            ITEM_TYPE
        } else {
            PROGRESS_TYPE
        }
    }

    /**
     * Listener for when a connection is clicked
     */
    interface OnConnectionClickListener {
        /**
         * Called when a connection is clicked
         *
         * @param connection The clicked connection
         */
        fun onConnectionClicked(connection: Connection)
    }

    interface Listener {
        fun onLoadBelow(adapter: ConnectionListAdapter, pageToLoad: Int): Boolean

        fun onLoadAbove(adapter: ConnectionListAdapter, pageToLoad: Int): Boolean
    }

    private class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class OnViewHolderClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            if (v.tag != null) {
                val connectionViewHolder = v.tag as ConnectionViewHolder
                val position = connectionViewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return
                }
                val connection = getItemAt(position)
                mOnConnectionClickListener?.onConnectionClicked(connection)
            }
        }
    }

    private class UICallback(val adapter: ConnectionListAdapter) : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            return when (msg.what) {
                MESSAGE_LOAD_MORE_BOTTOM -> {
                    val loading = adapter.onLoadMoreListener?.onLoadBelow(adapter, adapter.highestPage + 1) == true
                    adapter.loadingBottom = loading
                    true
                }
                MESSAGE_LOAD_MORE_TOP -> {
                    val loading = adapter.onLoadMoreListener?.onLoadAbove(adapter, adapter.lowestPage - 1) == true;
                    adapter.loadingTop = loading
                    true
                }
                else -> false
            }
        }
    }
}
