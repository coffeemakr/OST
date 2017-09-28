package ch.unstable.ost;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.QueryHistoryDao;
import ch.unstable.ost.database.model.QueryHistory;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static ch.unstable.ost.views.lists.query.QueryBinderKt.bindQuery;

public class LastQueryCardFragment extends QuickstartCardFragment {
    private static final String TAG = "LastQueryCardFragment";
    private View mCardLastQuery;
    private QueryHistoryDao mQueryDao;
    private TextView mLastQueryFromTo;
    private TextView mLastQueryDate;
    private OnQuerySelectedListener mOnQuerySelectedListener;
    private QueryHistory mLastQuery;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mQueryDao == null) {
            mQueryDao = Databases.getCacheDatabase(getContext()).queryHistoryDao();
        }
        mCompositeDisposable = new CompositeDisposable();
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
        return inflater.inflate(R.layout.card_last_query, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardLastQuery = view.findViewById(R.id.cardLastQuery);
        mCardLastQuery.setVisibility(View.GONE);
        mLastQueryFromTo = view.findViewById(R.id.lastQueryFromTo);
        mLastQueryDate = view.findViewById(R.id.lastQueryDate);

        Button buttonMore = view.findViewById(R.id.buttonMore);
        buttonMore.setOnClickListener(eventView -> onShowMoreQueries());

        Button buttonLoad = view.findViewById(R.id.buttonOpen);
        buttonLoad.setOnClickListener(eventView -> onOpenQuery());
    }

    private void onOpenQuery() {
        if (mOnQuerySelectedListener == null) {
            Log.w(TAG, "mOnQuerySelectedListener is null");
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(mLastQuery, "mLastQuery is null");
        mOnQuerySelectedListener.onRouteSelected(mLastQuery.getQuery());
    }

    private void onShowMoreQueries() {
        Intent intent = new Intent(getContext(), QueryHistoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Disposable disposable = mQueryDao.getLatestQuery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateLatestQuery, getErrorConsumer());
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @MainThread
    private void updateLatestQuery(QueryHistory queryHistory) {
        mCardLastQuery.setVisibility(View.VISIBLE); // TODO animate
        mLastQuery = queryHistory;
        bindQuery(queryHistory, mLastQueryDate, mLastQueryFromTo);
    }

    @Override
    protected int getErrorMessage() {
        return R.string.error_failed_to_load_last_query;
    }

    public interface OnQuerySelectedListener {
        @MainThread
        void onRouteSelected(@NonNull ConnectionQuery query);
    }
}
