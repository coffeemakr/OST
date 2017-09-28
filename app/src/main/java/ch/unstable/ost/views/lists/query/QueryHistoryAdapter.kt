package ch.unstable.ost.views.lists.query

import android.view.View
import ch.unstable.ost.R
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter
import com.google.common.base.Verify


class QueryHistoryAdapter(private val onClickListener: View.OnClickListener) : SingleTypeSimplerAdapter<QueryHistory, QueryViewHolder>() {


    override fun onBindViewHolder(viewHolder: QueryViewHolder, element: QueryHistory, position: Int) {
        bindQuery(element, viewHolder.date, viewHolder.fromAndTo)
        with(viewHolder.itemView) {
            tag = element
            isClickable = true
            setOnClickListener(onClickListener)
        }
    }

    override val layout: Int = R.layout.item_connection_query;

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(itemView: View): QueryViewHolder {
        return QueryViewHolder(itemView)
    }

    override fun getItemId(position: Int): Long {
        val id = getItem(position).id
        if(id == 0L) {

        }
        Verify.verify(id != 0L, "id is 0")
        return id
    }
}
