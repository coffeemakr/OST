package ch.unstable.ost;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.unstable.ost.database.Databases;
import ch.unstable.ost.database.dao.FavoriteConnectionDao;
import ch.unstable.ost.views.lists.favorite.FavoritesAdapter;
import ch.unstable.ost.utils.NavHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritesActivity extends AppCompatActivity {

    private FavoriteConnectionDao mFavoritesDao;
    private FavoritesAdapter mFavoritesAdapter;
    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFavoritesAdapter = new FavoritesAdapter();
        RecyclerView favorites = findViewById(R.id.favorites);
        favorites.setAdapter(mFavoritesAdapter);
        favorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mDisposable = new CompositeDisposable();
        mFavoritesDao = Databases
                .getCacheDatabase(this)
                .favoriteConnectionDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disposable = mFavoritesDao.getFavoriteConnections()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mFavoritesAdapter, throwable -> NavHelper.INSTANCE.startErrorActivity(FavoritesActivity.this, throwable));
        mDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

}
