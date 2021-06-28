package ch.unstable.ost;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.AttrRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.EmptyResultSetException;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.database.model.FavoriteConnection;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Fragment showing a single connection
 * <p>
 * A connection is from a station to another via one or more sections.
 */
public class ConnectionDetailFragment extends Fragment {

    public static final long NO_FAVORITE_ID = 0L;
    private static final String KEY_CONNECTION = "KEY_CONNECTION";
    private static final String KEY_FAVORITE_ID = "KEY_FAVORITE_ID";
    @AttrRes
    private static final int ICON_NO_FAVORITE = R.attr.ic_star_border_24dp_no_vector;
    @AttrRes
    private static final int ICON_FAVORITED = R.attr.ic_star_24dp_no_vector;
    private static final String TAG = "ConnectionDetailFgmt";
    private Connection connection;
    private SectionListAdapter sectionListAdapter;
    private SectionListAdapter.OnSectionClickedListener sectionClickedListener;
    private OnConnectionDetailInteractionListener connectionDetailInteractionListener;
    private FavoriteConnectionDao favoriteConnectionDao;
    private CompositeDisposable compositeDisposable;
    @Nullable
    private MenuItem menuItem;
    private long favoriteId;
    private Drawable[] styledIcons;

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
    public static ConnectionDetailFragment newInstance(@NonNull Connection connection, long favoriteId) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_CONNECTION, connection);
        if (favoriteId != 0) {
            arguments.putLong(KEY_FAVORITE_ID, favoriteId);
        }
        ConnectionDetailFragment detailFragment = new ConnectionDetailFragment();
        detailFragment.setArguments(arguments);
        return detailFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.connectionDetailInteractionListener = (OnConnectionDetailInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        connectionDetailInteractionListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            connection = savedInstanceState.getParcelable(KEY_CONNECTION);
            favoriteId = savedInstanceState.getLong(KEY_FAVORITE_ID, NO_FAVORITE_ID);
        } else {
            connection = getArguments().getParcelable(KEY_CONNECTION);
            favoriteId = getArguments().getLong(KEY_FAVORITE_ID, NO_FAVORITE_ID);
        }

        if (sectionClickedListener == null) {
            sectionClickedListener = section -> {
                if (connectionDetailInteractionListener != null) {
                    connectionDetailInteractionListener.onSectionSelected(section);
                }
            };
        }
        sectionListAdapter = new SectionListAdapter();
        sectionListAdapter.setOnJourneyClickedListener(sectionClickedListener);
        sectionListAdapter.setSections(connection.getSections());

        favoriteConnectionDao = Databases.getCacheDatabase(getContext()).favoriteConnectionDao();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONNECTION, connection);
        outState.putLong(KEY_FAVORITE_ID, favoriteId);
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
        menuItem = menu.findItem(R.id.action_favorite);
        if (favoriteId == NO_FAVORITE_ID) {
            setFavoriteIcon(false);
        } else {
            setFavoriteIcon(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            onToggedFavoriteConnection();
        }
        return super.onOptionsItemSelected(item);
    }

    @MainThread
    private void onToggedFavoriteConnection() {
        if (favoriteId == NO_FAVORITE_ID) {
            enableFavorite();
        } else {
            disableFavorite();
        }
    }

    @MainThread
    private void disableFavorite() {
        if (favoriteId == NO_FAVORITE_ID) return;
        Disposable disposable = Flowable.just(favoriteId)
                .subscribeOn(Schedulers.io())
                .singleOrError()
                .flatMap(favoriteId -> favoriteConnectionDao.getFavoriteById(favoriteId))
                .map(favoriteConnection -> {
                    favoriteConnectionDao.removeConnectionById(favoriteConnection);
                    return true;
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyResultSetException) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "Favorite already deleted", throwable);
                        return Single.just(true);
                    } else {
                        return Single.error(throwable);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> onFavoriteCleared());
        compositeDisposable.add(disposable);
    }

    private void setFavoriteIcon(boolean favoriteEnabled) {
        if (menuItem == null) {
            if (BuildConfig.DEBUG) Log.w(TAG, "Can't set icon (mFavoriteMenuItem is null)");
            return;
        }
        if (styledIcons == null) {
            int[] startIcon = new int[]{ICON_FAVORITED, ICON_NO_FAVORITE};
            TypedValue typedValue = new TypedValue();
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, startIcon);
            styledIcons = new Drawable[]{a.getDrawable(0), a.getDrawable(1)};
            a.recycle();
        }
        menuItem.setIcon(styledIcons[favoriteEnabled ? 0 : 1]);
    }

    @MainThread
    private void onFavoriteStored(long id) {
        setFavoriteIcon(true);
        favoriteId = id;
    }

    @MainThread
    private void onFavoriteCleared() {
        favoriteId = NO_FAVORITE_ID;
        setFavoriteIcon(false);
    }

    @MainThread
    private void enableFavorite() {
        Disposable disposable = Flowable.just(connection)
                .subscribeOn(Schedulers.io())
                .map(connection -> {
                    FavoriteConnection favoriteConnection = new FavoriteConnection(connection);
                    long id = favoriteConnectionDao.addConnection(favoriteConnection);
                    favoriteConnection.setId(id);
                    return favoriteConnection;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteConnection -> onFavoriteStored(favoriteConnection.getId())); // TODO: error handling
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroyOptionsMenu() {
        menuItem = null;
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView sectionList = view.findViewById(R.id.sectionsList);
        sectionList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        sectionList.setAdapter(sectionListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        sectionList.addItemDecoration(dividerItemDecoration);
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
