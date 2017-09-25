package ch.unstable.ost;


import android.arch.persistence.room.EmptyResultSetException;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.database.model.FavoriteConnection;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Fragment showing a single connection
 * <p>
 * A connection is from a station to another via one or more sections.
 */
public class ConnectionDetailFragment extends Fragment {


    private static final String KEY_CONNECTION = "KEY_CONNECTION";
    private static final String KEY_FAVORITE_ID = "KEY_FAVORITE_ID";
    public static final long NO_FAVORITE_ID = 0L;
    @AttrRes
    private static final int ICON_NO_FAVORITE = R.attr.ic_star_border_24dp_no_vector;
    @AttrRes
    private static final int ICON_FAVORITED = R.attr.ic_star_24dp_no_vector;
    private static final String TAG = "ConnectionDetailFgmt";
    private Connection mConnection;
    private RecyclerView mSectionsList;
    private SectionListAdapter mSectionListAdapter;
    private SectionListAdapter.OnSectionClickedListener mOnJourneyClickedListener;
    private OnConnectionDetailInteractionListener mOnConnectionDetailInteractionListener;
    private FavoriteConnectionDao mFavoriteConnectionDao;
    private CompositeDisposable mDisposable;
    @Nullable
    private MenuItem mFavoriteMenuItem;
    private long mFavoriteId;
    private Drawable[] mStyledIcons;

    public ConnectionDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance
     *
     * @param connection the connection to show
     * @param favoriteId the favorite id or 0
     * @return the fragment
     */
    public static ConnectionDetailFragment newInstance(Connection connection, long favoriteId) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(connection, "connection is null");
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_CONNECTION, connection);
        if(favoriteId != 0) {
            arguments.putLong(KEY_FAVORITE_ID, favoriteId);
        }
        ConnectionDetailFragment detailFragment = new ConnectionDetailFragment();
        detailFragment.setArguments(arguments);
        return detailFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mOnConnectionDetailInteractionListener = (OnConnectionDetailInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnConnectionDetailInteractionListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposable = new CompositeDisposable();
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            mConnection = savedInstanceState.getParcelable(KEY_CONNECTION);
            mFavoriteId = savedInstanceState.getLong(KEY_FAVORITE_ID, NO_FAVORITE_ID);
        } else {
            mConnection = getArguments().getParcelable(KEY_CONNECTION);
            mFavoriteId = getArguments().getLong(KEY_FAVORITE_ID, NO_FAVORITE_ID);
        }

        if (mOnJourneyClickedListener == null) {
            mOnJourneyClickedListener = new SectionListAdapter.OnSectionClickedListener() {
                @Override
                public void onSectionClicked(@NonNull Section section) {
                    if (mOnConnectionDetailInteractionListener != null) {
                        mOnConnectionDetailInteractionListener.onSectionSelected(section);
                    }
                }
            };
        }
        mSectionListAdapter = new SectionListAdapter();
        mSectionListAdapter.setOnJourneyClickedListener(mOnJourneyClickedListener);
        mSectionListAdapter.setSections(mConnection.getSections());

        mFavoriteConnectionDao = Databases.getCacheDatabase(getContext()).favoriteConnectionDao();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONNECTION, mConnection);
        outState.putLong(KEY_FAVORITE_ID, mFavoriteId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.connection_detail_menu, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
        if(mFavoriteId == NO_FAVORITE_ID) {
            setFavoriteIcon(false);
        } else {
            setFavoriteIcon(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_favorite) {
            onToggedFavoriteConnection();
        }
        return super.onOptionsItemSelected(item);
    }

    @MainThread
    private void onToggedFavoriteConnection() {
        if(mFavoriteId == NO_FAVORITE_ID) {
            enableFavorite();
        } else {
            disableFavorite();
        }
    }

    @MainThread
    private void disableFavorite() {
        if(mFavoriteId == NO_FAVORITE_ID) return;
        Disposable disposable = Flowable.just(mFavoriteId)
                .subscribeOn(Schedulers.io())
                .singleOrError()
                .flatMap(new Function<Long, Single<FavoriteConnection>>() {
                    @Override
                    public Single<FavoriteConnection> apply(Long favoriteId) throws Exception {
                        return mFavoriteConnectionDao.getFavoriteById(favoriteId);
                    }
                })
                .map(new Function<FavoriteConnection, Boolean>() {
                    @Override
                    public Boolean apply(FavoriteConnection favoriteConnection) throws Exception {
                        mFavoriteConnectionDao.removeConnectionById(favoriteConnection);
                        return true;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<Boolean>>() {
                    @Override
                    public SingleSource<Boolean> apply(@NonNull Throwable throwable) throws Exception {
                        if(throwable instanceof EmptyResultSetException) {
                            if(BuildConfig.DEBUG) Log.d(TAG, "Favorite already deleted", throwable);
                            return Single.just(true);
                        } else {
                            return Single.error(throwable);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean ignored) throws Exception {
                        onFavoriteCleared();
                    }
                });
        mDisposable.add(disposable);
    }

    private void setFavoriteIcon(boolean favoriteEnabled) {
        if(mFavoriteMenuItem == null) {
            if(BuildConfig.DEBUG) Log.w(TAG, "Can't set icon (mFavoriteMenuItem is null)");
            return;
        }
        if(mStyledIcons == null) {
            int[] startIcon = new int[] { ICON_FAVORITED, ICON_NO_FAVORITE};
            TypedValue typedValue = new TypedValue();
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, startIcon);
            mStyledIcons = new Drawable[]{a.getDrawable(0), a.getDrawable(1)};
            a.recycle();
        }
        mFavoriteMenuItem.setIcon(mStyledIcons[favoriteEnabled ? 0 : 1]);
    }

    @MainThread
    private void onFavoriteStored(long id) {
        setFavoriteIcon(true);
        mFavoriteId = id;
    }

    @MainThread
    private void onFavoriteCleared() {
        mFavoriteId = NO_FAVORITE_ID;
        setFavoriteIcon(false);
    }

    @MainThread
    private void enableFavorite() {
        Disposable disposable = Flowable.just(mConnection)
                .subscribeOn(Schedulers.io())
                .map(new Function<Connection, FavoriteConnection>() {
                    @Override
                    public FavoriteConnection apply(Connection connection) throws Exception {
                        FavoriteConnection favoriteConnection = new FavoriteConnection(connection);
                        long id = mFavoriteConnectionDao.addConnection(favoriteConnection);
                        favoriteConnection.setId(id);
                        return favoriteConnection;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FavoriteConnection>() {
                    @Override
                    public void accept(FavoriteConnection favoriteConnection) throws Exception {
                        onFavoriteStored(favoriteConnection.getId());
                    }
                }); // TODO: error handling
        mDisposable.add(disposable);
    }

    @Override
    public void onDestroyOptionsMenu() {
        mFavoriteMenuItem = null;
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSectionsList = view.findViewById(R.id.sectionsList);
        mSectionsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSectionsList.setAdapter(mSectionListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mSectionsList.addItemDecoration(dividerItemDecoration);
    }

    public interface OnConnectionDetailInteractionListener {
        /**
         * Called when a section is selected
         *
         * @param section the section
         */
        @MainThread
        void onSectionSelected(@NonNull Section section);
    }
}
