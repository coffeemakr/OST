package ch.unstable.ost;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.lists.favorite.FavoritesAdapter;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView mFavorites;
    private FavoriteConnectionDao mFavoritesDao;
    private FlowableSubscriber<? super List<FavoriteConnection>> favoritesConsumer;
    private FavoritesAdapter mFavoritesAdapter;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFavoritesAdapter = new FavoritesAdapter();
        mFavorites = findViewById(R.id.favorites);

        mFavoritesDao = Databases
                .getCacheDatabase(this)
                .favoriteConnectionDao();

        mDisposable = mFavoritesDao.getFavoriteConnections()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mFavoritesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

}
