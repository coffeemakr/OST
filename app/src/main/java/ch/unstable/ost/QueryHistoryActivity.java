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
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import ch.unstable.ost.lists.query.QueryHistoryAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryHistoryActivity extends AppCompatActivity {

    private static final String TAG = "QueryHistoryActivity";
    private QueryHistoryDao mQueryHistoryDao;
    private QueryHistoryAdapter mQueryHistoryAdapter;
    private View.OnClickListener mOnQueryHistoryItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            QueryHistory query = (QueryHistory) view.getTag();
            //noinspection ResultOfMethodCallIgnored
            checkNotNull(query, "query");
            openConnection(query);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_history);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        checkNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                if (BuildConfig.DEBUG) Log.d(TAG, "Got entries: " + entries);
                if (mQueryHistoryAdapter != null) {
                    mQueryHistoryAdapter.setEntries(entries);
                } else {
                    Log.w(TAG, "Adapter is null");
                }
            }
        };
    }
}
