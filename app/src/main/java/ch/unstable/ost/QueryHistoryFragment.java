package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import ch.unstable.ost.utils.LocalizationUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Fragment to display old connections
 */
public class QueryHistoryFragment extends Fragment{
    private static final String TAG = "QueryHistoryFragment";
    private QueryHistoryDao mQueryHistoryDao;
    private QueryHistoryAdapter mQueryHistoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueryHistoryDao = Databases.getCacheDatabase(getContext()).queryHistoryDao();
        Disposable disposable = mQueryHistoryDao.getConnections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getQueriesConsumer());
        // TODO: is disposable required?

        if(mQueryHistoryAdapter != null) {
            mQueryHistoryAdapter = new QueryHistoryAdapter();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView cachedConnections  = view.findViewById(R.id.cachedConnections);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        cachedConnections.setLayoutManager(layoutManager);
        cachedConnections.setAdapter(mQueryHistoryAdapter);
    }

    /**
     * Get a consumer which adds the loaded queries into the adapter
     * @return the consumer
     */
    @NonNull
    public Consumer<List<QueryHistory>> getQueriesConsumer() {
        return new Consumer<List<QueryHistory>>() {
            @Override
            public void accept(List<QueryHistory> entries) throws Exception {
                if(mQueryHistoryAdapter != null) {
                    mQueryHistoryAdapter.setEntries(entries);
                } else {
                    Log.w(TAG, "Adapter is null");
                }
            }
        };
    }


    private static class QueryViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView fromAndTo;

        public QueryViewHolder(View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.date_text);
            this.fromAndTo = itemView.findViewById(R.id.from_to_text);
        }
    }

    private static class QueryHistoryAdapter extends RecyclerView.Adapter<QueryViewHolder> {

        private ArrayList<QueryHistory> mHistoryItems;

        public QueryHistoryAdapter() {
            mHistoryItems = new ArrayList<>();
        }

        @Override
        public QueryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_connection_query, viewGroup, false);
            return new QueryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(QueryViewHolder viewHolder, int i) {
            final Context context = viewHolder.itemView.getContext();
            QueryHistory queryEntry = getQueryHistoryAt(i);
            ConnectionQuery query = queryEntry.getQuery();
            Date arrival = query.getArrivalTime();
            Date departure = query.getDepartureTime();
            if(query.isNow()) {
                // The search was for "now" so we show the time the query was submitted
                departure = queryEntry.getCreationDate();
            }
            viewHolder.date.setText(LocalizationUtils.getArrivalOrDepartureText(context, arrival, departure));
            viewHolder.fromAndTo.setText(context.getString(R.string.fromAndTo, query.getFrom(), query.getTo()));
        }

        public QueryHistory getQueryHistoryAt(int i) {
            return mHistoryItems.get(i);
        }

        @Override
        public int getItemCount() {
            return mHistoryItems.size();
        }

        public void setEntries(List<QueryHistory> entries) {
            mHistoryItems = new ArrayList<>(entries);
            notifyDataSetChanged();
        }
    }
}
