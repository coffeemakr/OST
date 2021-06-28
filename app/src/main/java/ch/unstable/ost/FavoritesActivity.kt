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
import io.reactivex.schedulers.Schedulers

class FavoritesActivity : AppCompatActivity() {
    private var favoritesDao: FavoriteConnectionDao? = null
    private var favoritesAdapter: FavoritesAdapter? = null
    private var compositeDisposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        favoritesAdapter = FavoritesAdapter()
        val favorites = findViewById<RecyclerView>(R.id.favorites)
        favorites.adapter = favoritesAdapter
        favorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        compositeDisposable = CompositeDisposable()
        favoritesDao = Databases
                .getCacheDatabase(this)
                .favoriteConnectionDao()
    }

    override fun onResume() {
        super.onResume()
        val disposable = favoritesDao!!.getFavoriteConnections()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoritesAdapter, { throwable: Throwable? -> startErrorActivity(this@FavoritesActivity, throwable!!) })
        compositeDisposable!!.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.dispose()
    }
}