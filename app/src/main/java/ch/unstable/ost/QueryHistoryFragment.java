package ch.unstable.ost;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import java.util.ArrayList;
import java.util.List;

import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Fragment to display old connections
 */
public class QueryHistoryFragment extends Fragment {
    private static final String TAG = "QueryHistoryFragment";
    private QueryHistoryDao mQueryHistoryDao;
    private QueryHistoryAdapter mQueryHistoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueryHistoryDao = Databases.getCacheDatabase(getContext()).queryHistoryDao();

        if (mQueryHistoryAdapter == null) {
            mQueryHistoryAdapter = new QueryHistoryAdapter();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disposable = mQueryHistoryDao.getConnections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getQueriesConsumer());
        // TODO: is disposable required?
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView historyEntries = view.findViewById(R.id.historyEntries);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyEntries.setLayoutManager(layoutManager);
        historyEntries.setAdapter(mQueryHistoryAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(
                historyEntries.getContext(),
                layoutManager.getOrientation()
        );

        historyEntries.addItemDecoration(divider);
    }

    /**
     * Get a consumer which adds the loaded queries into the adapter
     *
     * @return the consumer
     */
    @NonNull
    public Consumer<List<QueryHistory>> getQueriesConsumer() {
        return new Consumer<List<QueryHistory>>() {
            @Override
            public void accept(List<QueryHistory> entries) throws Exception {
                if(BuildConfig.DEBUG) Log.d(TAG, "Got entries: " + entries);
                if (mQueryHistoryAdapter != null) {
                    mQueryHistoryAdapter.setEntries(entries);
                } else {
                    Log.w(TAG, "Adapter is null");
                }
            }
        };
    }


    /**
     * View holder for a query history entry
     */
    private static class QueryViewHolder extends RecyclerView.ViewHolder {
        /**
         * Text field containing the description of the time restriction
         * e.g. "Departure 11:20"
         */
        private final TextView date;
        /**
         * Text field containing the description of the route.
         * e.g "From Zürich to Basel SBB"
         */
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
            setHasStableIds(true);
        }

        @Override
        public QueryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_connection_query, viewGroup, false);
            return new QueryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(QueryViewHolder viewHolder, int i) {
            QueryHistory queryEntry = getQueryHistoryAt(i);
            QueryBinder.bindDate(queryEntry, viewHolder.date, viewHolder.fromAndTo);
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
}
