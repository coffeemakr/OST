package ch.unstable.ost;

import android.arch.persistence.room.EmptyResultSetException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding2.view.RxView;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class QuickstartFragment extends Fragment {
    private static final String TAG = "QuickstartFragment";
    private View mCardLastQuery;
    private QueryHistoryDao mQueryDao;
    private TextView mLastQueryFromTo;
    private TextView mLastQueryDate;
    private OnQuerySelectedListener mOnQuerySelectedListener;
    private QueryHistory mLastQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mQueryDao == null) {
            mQueryDao = Databases.getCacheDatabase(getContext()).queryHistoryDao();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Preconditions.checkState(
                context instanceof OnQuerySelectedListener,
                "context must implement OnQuerySelectedListener");
        mOnQuerySelectedListener = (OnQuerySelectedListener) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mOnQuerySelectedListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quickstart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardLastQuery = view.findViewById(R.id.cardLastQuery);
        mCardLastQuery.setVisibility(View.GONE);
        mLastQueryFromTo = view.findViewById(R.id.lastQueryFromTo);
        mLastQueryDate = view.findViewById(R.id.lastQueryDate);

        Button buttonMore = view.findViewById(R.id.buttonMore);
        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowMore();
            }
        });

        Button buttonLoad = view.findViewById(R.id.buttonOpen);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenConnection();
            }
        });
    }

    private void onOpenConnection() {
        Intent intent = new Intent(getContext(), QueryHistoryActivity.class);
        startActivity(intent);
    }

    private void onShowMore() {
        if(mOnQuerySelectedListener == null) {
            Log.w(TAG, "mOnQuerySelectedListener is null");
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(mLastQuery, "mLastQuery is null");
        mOnQuerySelectedListener.onRouteSelected(mLastQuery.getQuery());
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disposable = mQueryDao.getLatestQuery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QueryHistory>() {
                               @Override
                               public void accept(QueryHistory queryHistory) throws Exception {
                                   updateLatestQuery(queryHistory);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable instanceof EmptyResultSetException) {
                                    // Nothing to do?
                                    Log.d(TAG, "No last query", throwable);
                                } else {
                                    Log.e(TAG, "Error while loading last query", throwable);
                                }
                                mCardLastQuery.setVisibility(View.GONE);
                            }
                        });
    }

    @MainThread
    private void updateLatestQuery(QueryHistory queryHistory) {
        mCardLastQuery.setVisibility(View.VISIBLE); // TODO animate
        mLastQuery = queryHistory;
        QueryBinder.bindDate(queryHistory, mLastQueryDate, mLastQueryFromTo);
    }

    public interface OnQuerySelectedListener {
        @MainThread
        void onRouteSelected(@NonNull ConnectionQuery query);
    }
}
