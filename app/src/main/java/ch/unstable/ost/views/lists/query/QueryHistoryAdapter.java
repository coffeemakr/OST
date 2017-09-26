package ch.unstable.ost.views.lists.query;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import java.util.ArrayList;
import java.util.List;

import ch.unstable.ost.R;
import ch.unstable.ost.database.model.QueryHistory;


public class QueryHistoryAdapter extends RecyclerView.Adapter<QueryViewHolder> {

    private final View.OnClickListener mOnClickListener;
    private ArrayList<QueryHistory> mHistoryItems;

    public QueryHistoryAdapter(View.OnClickListener onClickListener) {
        mHistoryItems = new ArrayList<>();
        setHasStableIds(true);
        mOnClickListener = Preconditions.checkNotNull(onClickListener, "onClickListener");
    }

    @Override
    public QueryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_connection_query, viewGroup, false);
        return new QueryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QueryViewHolder viewHolder, int i) {
        QueryHistory queryEntry = getQueryHistoryAt(i);
        QueryBinder.INSTANCE.bindQuery(queryEntry, viewHolder.getDate(), viewHolder.getFromAndTo());
        viewHolder.itemView.setTag(queryEntry);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(mOnClickListener);
    }

    @NonNull
    public QueryHistory getQueryHistoryAt(int i) {
        return mHistoryItems.get(i);
    }

    @Override
    public long getItemId(int position) {
        long id = getQueryHistoryAt(position).getId();
        Verify.verify(id != 0, "id is 0");
        return id;
    }

    @Override
    public int getItemCount() {
        return mHistoryItems.size();
    }

    public void setEntries(List<QueryHistory> entries) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(entries, "entries is null");
        mHistoryItems = new ArrayList<>(entries);
        notifyDataSetChanged();
    }
}
