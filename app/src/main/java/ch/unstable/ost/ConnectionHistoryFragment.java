package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
    import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.CachedConnectionDAO;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.model.CachedConnection;
import ch.unstable.ost.utils.LocalizationUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Fragment to display old connections
 */
public class ConnectionHistoryFragment extends Fragment{
    private CachedConnectionDAO mCachedConnectionsDao;
    private CachedConnectionsAdapter mCachedConnectionsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCachedConnectionsDao = Databases.getCacheDatabase(getContext()).cachedConnectionDao();
        Disposable disposable = mCachedConnectionsDao.getConnections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getCachedConnectionsConsumer());

        if(mCachedConnectionsAdapter != null) {
            mCachedConnectionsAdapter = new CachedConnectionsAdapter();
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
        cachedConnections.setAdapter(mCachedConnectionsAdapter);
    }

    public Consumer<CachedConnection> getCachedConnectionsConsumer() {
        return new Consumer<CachedConnection>() {
            @Override
            public void accept(CachedConnection cachedConnection) throws Exception {
                if(mCachedConnectionsAdapter != null) {
                    mCachedConnectionsAdapter.addCachedConnection(cachedConnection);
                }
            }
        };
    }


    private static class ConnectionHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView fromAndTo;

        public ConnectionHistoryViewHolder(View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.date_text);
            this.fromAndTo = itemView.findViewById(R.id.from_to_text);
        }
    }

    private static class CachedConnectionsAdapter extends RecyclerView.Adapter<ConnectionHistoryViewHolder> {

        private final ArrayList<CachedConnection> mHistoryItems;

        public CachedConnectionsAdapter() {
            mHistoryItems = new ArrayList<>();
        }

        @Override
        public ConnectionHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_connection_query, viewGroup, false);
            return new ConnectionHistoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ConnectionHistoryViewHolder viewHolder, int i) {
            final Context context = viewHolder.itemView.getContext();
            CachedConnection cachedConnection = getConnectionAtPosition(i);
            ConnectionQuery query = cachedConnection.getQuery();
            viewHolder.date.setText(LocalizationUtils.getArrivalOrDepartureText(context, query));
            viewHolder.fromAndTo.setText(context.getString(R.string.fromAndTo, query.getFrom(), query.getTo()));
        }

        public CachedConnection getConnectionAtPosition(int i) {
            return mHistoryItems.get(i);
        }

        @Override
        public int getItemCount() {
            return mHistoryItems.size();
        }

        public void addCachedConnection(CachedConnection connection) {
            mHistoryItems.add(connection);
            notifyItemInserted(mHistoryItems.size() - 1);
        }
    }
}
