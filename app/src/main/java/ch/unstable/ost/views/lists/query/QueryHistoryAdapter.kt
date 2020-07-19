package ch.unstable.ost.views.lists.query

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.R
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.views.lists.query.QueryBinder.bindQuery
import java.util.*

class QueryHistoryAdapter(private val mOnClickListener: View.OnClickListener) : RecyclerView.Adapter<QueryViewHolder>() {
    private var mHistoryItems: ArrayList<QueryHistory>
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): QueryViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_connection_query, viewGroup, false)
        return QueryViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: QueryViewHolder, i: Int) {
        val queryEntry = getQueryHistoryAt(i)
        bindQuery(queryEntry, viewHolder.date, viewHolder.fromAndTo)
        viewHolder.itemView.tag = queryEntry
        viewHolder.itemView.isClickable = true
        viewHolder.itemView.setOnClickListener(mOnClickListener)
    }

    fun getQueryHistoryAt(i: Int): QueryHistory {
        return mHistoryItems[i]
    }

    override fun getItemId(position: Int): Long {
        val id = getQueryHistoryAt(position).id
        require(id == 0L) { "id is 0" }
        return id
    }

    override fun getItemCount(): Int {
        return mHistoryItems.size
    }

    fun setEntries(entries: List<QueryHistory>) {
        mHistoryItems = ArrayList(entries)
        notifyDataSetChanged()
    }

    init {
        mHistoryItems = ArrayList()
        setHasStableIds(true)
    }
}