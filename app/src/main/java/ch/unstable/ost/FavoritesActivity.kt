package ch.unstable.ost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.FavoriteConnectionDao
import ch.unstable.ost.utils.NavHelper.startErrorActivity
import ch.unstable.ost.views.lists.favorite.FavoritesAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class FavoritesActivity : AppCompatActivity() {
    private var mFavoritesDao: FavoriteConnectionDao? = null
    private var mFavoritesAdapter: FavoritesAdapter? = null
    private var mDisposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mFavoritesAdapter = FavoritesAdapter()
        val favorites = findViewById<RecyclerView>(R.id.favorites)
        favorites.adapter = mFavoritesAdapter
        favorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mDisposable = CompositeDisposable()
        mFavoritesDao = Databases
                .getCacheDatabase(this)
                .favoriteConnectionDao()
    }

    override fun onResume() {
        super.onResume()
        val disposable = mFavoritesDao!!.favoriteConnections
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mFavoritesAdapter, Consumer { throwable: Throwable? -> startErrorActivity(this@FavoritesActivity, throwable!!) })
        mDisposable!!.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable!!.dispose()
    }
}