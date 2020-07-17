package ch.unstable.ost.views.lists.connection

import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.R
import ch.unstable.ost.api.model.Connection
import com.google.common.base.Preconditions
import java.util.*

class ConnectionListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mConnections: MutableList<Connection> = ArrayList()
    private val mOnViewHolderClickListener: View.OnClickListener = OnViewHolderClickListener()
    private val mHandler: Handler
    private var mOnConnectionClickListener: OnConnectionClickListener? = null
    private var mListener: Listener? = null
    private var loadingTop = false
    private var loadingBottom = false
    var highestPage = 0
        private set
    var lowestPage = 0
        private set

    private fun setLoadingTop(loadingTop: Boolean) {
        if (this.loadingTop != loadingTop) {
            this.loadingTop = loadingTop
            if (loadingTop) {
                notifyItemInserted(0)
            } else {
                notifyItemRemoved(0)
            }
        }
    }

    private fun setLoadingBottom(loadingBottom: Boolean) {
        if (this.loadingBottom != loadingBottom) {
            val itemsBefore = itemCount
            this.loadingBottom = loadingBottom
            if (loadingBottom) {
                notifyItemInserted(itemsBefore)
            } else {
                notifyItemRemoved(itemsBefore - 1)
            }
        }
    }

    fun createOnScrollListener(linearLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener {
        val visibleThreshold = 1
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mListener == null) return
                val lastItemPostion = linearLayoutManager.itemCount - 1
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (dy >= 0 && !loadingBottom && lastItemPostion <= lastVisibleItem + visibleThreshold) {
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
            mConnections.clear()
            Collections.addAll(mConnections, *connections)
            notifyDataSetChanged()
        } else if (page > 0) {
            appendConnections(connections)
        } else if (page < 0) {
            prependConnections(connections)
        }
    }

    @MainThread
    fun appendConnections(connections: Array<Connection>) {
        Preconditions.checkNotNull(connections, "connections is null")
        setLoadingBottom(false)
        val startPosition = itemCount
        var skipFirst = 0
        for (connection in mConnections) {
            Log.d(TAG, connections[skipFirst].toString() + "  ?= " + connection)
            if (connection == connections[skipFirst]) {
                ++skipFirst
            } else if (skipFirst > 0) {
                break
            }
        }
        Log.w(TAG, "Found duplicated items (" + skipFirst + ") -> adding only " + (connections.size - skipFirst))
        for (i in skipFirst until connections.size) {
            mConnections.add(connections[i])
        }
        notifyItemRangeInserted(startPosition, connections.size - skipFirst)
        ++highestPage
    }

    @MainThread
    fun prependConnections(connections: Array<Connection>) {
        Preconditions.checkNotNull(connections, "connections is null")
        setLoadingTop(false)
        val connectionsToInsert = ArrayList<Connection>(connections.size)
        Collections.addAll(connectionsToInsert, *connections)
        mConnections.addAll(0, connectionsToInsert)
        notifyItemRangeInserted(0, connections.size)
        --lowestPage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        return if (viewType == ITEM_TYPE) {
            view = inflater.inflate(R.layout.item_connection, parent, false)
            ConnectionViewHolder(view)
        } else {
            view = inflater.inflate(R.layout.item_progress, parent, false)
            ProgressViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE) {
            onBindViewHolder(holder as ConnectionViewHolder, position)
        }
    }

    fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val connection = getItemAt(position)
        ConnectionBinder.bindConnection(connection, holder)
        holder.itemView.tag = holder
        holder.itemView.setOnClickListener(mOnViewHolderClickListener)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ConnectionViewHolder) {
            holder.itemView.tag = null
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        var size = mConnections.size
        if (loadingTop) ++size
        if (loadingBottom) ++size
        return size
    }

    fun clearConnections() {
        mConnections.clear()
        notifyDataSetChanged()
    }

    @MainThread
    fun setOnConnectionClickListener(onConnectionClickListener: OnConnectionClickListener?) {
        mOnConnectionClickListener = onConnectionClickListener
    }

    fun setOnLoadMoreListener(listener: Listener?) {
        mListener = listener
    }

    private fun getItemAt(position: Int): Connection {
        var position = position
        if (loadingTop) {
            --position
        }
        return mConnections[position]
    }

    override fun getItemViewType(position: Int): Int {
        var position = position
        if (loadingTop) --position
        return if (position >= 0 && position < mConnections.size) {
            ITEM_TYPE
        } else {
            PROGRESS_TYPE
        }
    }

    val connections: Array<Connection>
        get() = mConnections.toTypedArray()

    val isEmpty: Boolean
        get() = mConnections.isEmpty()

    interface OnConnectionClickListener {
        fun onConnectionClicked(connection: Connection?)
    }

    interface Listener {
        fun onLoadBelow(adapter: ConnectionListAdapter?, pageToLoad: Int): Boolean
        fun onLoadAbove(adapter: ConnectionListAdapter?, pageToLoad: Int): Boolean
    }

    private class ProgressViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    class State(val connections: Array<Connection?>, val lowestPage: Int, val highestPage: Int)

    inner class OnViewHolderClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val connectionViewHolder = v.tag as ConnectionViewHolder
            val position = connectionViewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return
            }
            val connection: Connection
            connection = try {
                getItemAt(position)
            } catch (e: RuntimeException) {
                Log.e(TAG, "Failed to get connection", e)
                return
            }
            if (mOnConnectionClickListener != null) {
                mOnConnectionClickListener!!.onConnectionClicked(connection)
            }
        }
    }

    private inner class UICallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            return when (msg.what) {
                MESSAGE_LOAD_MORE_BOTTOM -> {
                    if (mListener!!.onLoadBelow(this@ConnectionListAdapter, highestPage + 1)) {
                        setLoadingBottom(true)
                    } else {
                        setLoadingBottom(false)
                    }
                    true
                }
                MESSAGE_LOAD_MORE_TOP -> {
                    if (mListener!!.onLoadAbove(this@ConnectionListAdapter, lowestPage - 1)) {
                        setLoadingTop(true)
                    } else {
                        setLoadingTop(false)
                    }
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        const val TAG = "ConnectionListAdapter"
        private const val PROGRESS_TYPE = 0
        private const val ITEM_TYPE = 1
        private const val MESSAGE_LOAD_MORE_BOTTOM = 0
        private const val MESSAGE_LOAD_MORE_TOP = 1
    }

    init {
        mHandler = Handler(UICallback())
    }
}