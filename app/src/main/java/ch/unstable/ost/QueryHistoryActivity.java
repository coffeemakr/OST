package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class QueryHistoryActivity extends AppCompatActivity {

    private static final String TAG = "QueryHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_history);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQueryHistoryDao = Databases.getCacheDatabase(this).queryHistoryDao();

        if (mQueryHistoryAdapter == null) {
            mQueryHistoryAdapter = new QueryHistoryAdapter(mOnQueryHistoryItemClickListener);
        }

        RecyclerView historyEntries = findViewById(R.id.historyEntries);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyEntries.setLayoutManager(layoutManager);
        historyEntries.setAdapter(mQueryHistoryAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(
                historyEntries.getContext(),
                layoutManager.getOrientation()
        );

        historyEntries.addItemDecoration(divider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private QueryHistoryDao mQueryHistoryDao;
    private QueryHistoryAdapter mQueryHistoryAdapter;
    private View.OnClickListener mOnQueryHistoryItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            QueryHistory query = (QueryHistory) view.getTag();
            //noinspection ResultOfMethodCallIgnored
            Preconditions.checkNotNull(query, "query");
            openConnection(query);
        }
    };

    private void openConnection(@NonNull QueryHistory query) {
        Intent intent = new Intent(this, ConnectionListActivity.class);
        intent.putExtra(ConnectionListActivity.EXTRA_CONNECTION_QUERY, query.getQuery());
        intent.setAction(Intent.ACTION_SEARCH);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disposable = mQueryHistoryDao.getConnections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getQueriesConsumer());
        // TODO: is disposable required?
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
            QueryBinder.bindQuery(queryEntry, viewHolder.date, viewHolder.fromAndTo);
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
}
