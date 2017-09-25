package ch.unstable.ost;

import android.arch.persistence.room.EmptyResultSetException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.lists.query.QueryBinder;
import ch.unstable.ost.utils.LocalizationUtils;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoriteCardFragment extends Fragment {

    private static final String TAG = "FavoriteCardFragment";
    private OnFavoriteSelectedListener mListener;
    private View mCardFavorites;
    private TextView mFavoriteFromTo;
    private TextView mFavoriteDate;
    private FavoriteConnectionDao mFavoriteDao;
    @Nullable
    private FavoriteConnection mLatestFavorite = null;
    private Disposable mDisposable;

    public FavoriteCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteDao = Databases.getCacheDatabase(getContext()).favoriteConnectionDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCardFavorites = view.findViewById(R.id.cardFavorites);
        mCardFavorites.setVisibility(View.GONE);
        mFavoriteFromTo = view.findViewById(R.id.favoriteFromTo);
        mFavoriteDate = view.findViewById(R.id.favoriteDate);

        View moreButton = view.findViewById(R.id.buttonMoreFavorites);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreButtonPressed();
            }
        });
        View openButton = view.findViewById(R.id.buttonOpenFavorite);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenButtonPressed();
            }
        });
    }

    private void bindConnection(FavoriteConnection favoriteConnection) {
        Connection connection = favoriteConnection.getConnection();
        QueryBinder.bindFromToText(mFavoriteFromTo, connection);
        mFavoriteDate.setText(LocalizationUtils.getDepartureText(getContext(), connection.getDepartureDate()));
        mCardFavorites.setVisibility(View.VISIBLE);
        mLatestFavorite = favoriteConnection;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if(mDisposable != null) mDisposable.dispose();
        mDisposable = mFavoriteDao.getLatestFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FavoriteConnection>() {
                    @Override
                    public void accept(FavoriteConnection favoriteConnection) throws Exception {
                        Log.d(TAG, "Got latest connection: " + favoriteConnection);
                        bindConnection(favoriteConnection);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mCardFavorites.setVisibility(View.GONE); // TODO: animate
                        if(throwable instanceof EmptyResultSetException) {
                            Log.d(TAG, "No last favorite");
                        } else {
                            Log.e(TAG, "Failed to load latest favorite: ", throwable);
                            // TODO: error handling
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.dispose();
    }

    private void onOpenButtonPressed() {
        if(mListener != null && mLatestFavorite != null) {
            mListener.onFavoriteSelected(mLatestFavorite);
        } else if(BuildConfig.DEBUG) {
            Log.w(TAG, "mListener or mLatestFavorite is null (" + mListener + ", " + mLatestFavorite + ")");
        }
    }

    private void onMoreButtonPressed() {
        Intent intent = new Intent(getContext(), FavoritesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteSelectedListener) {
            mListener = (OnFavoriteSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFavoriteSelectedListener {
        void onFavoriteSelected(@NonNull FavoriteConnection favoriteConnection);
    }
}
