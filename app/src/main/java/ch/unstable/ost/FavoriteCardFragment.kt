package ch.unstable.ost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.FavoriteConnectionDao
import ch.unstable.ost.database.model.FavoriteConnection
import ch.unstable.ost.utils.LocalizationUtils.getDepartureText
import ch.unstable.ost.views.lists.query.QueryBinder.bindFromToText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class FavoriteCardFragment : QuickstartCardFragment() {
    private var favoriteSelectedListener: OnFavoriteSelectedListener? = null
    private var cardFavorites: View? = null
    private var favoriteFromTo: TextView? = null
    private var favoriteDate: TextView? = null
    private var favoriteDao: FavoriteConnectionDao? = null
    private var latestFavorite: FavoriteConnection? = null
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteDao = Databases.getCacheDatabase(requireContext()).favoriteConnectionDao()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardFavorites = view.findViewById(R.id.cardFavorites)
        cardFavorites?.visibility = View.GONE
        favoriteFromTo = view.findViewById(R.id.favoriteFromTo)
        favoriteDate = view.findViewById(R.id.favoriteDate)
        val moreButton = view.findViewById<View>(R.id.buttonMoreFavorites)
        moreButton.setOnClickListener { onMoreButtonPressed() }
        val openButton = view.findViewById<View>(R.id.buttonOpenFavorite)
        openButton.setOnClickListener { onOpenButtonPressed() }
    }

    private fun bindConnection(favoriteConnection: FavoriteConnection) {
        val connection = favoriteConnection.connection
        bindFromToText(favoriteFromTo!!, connection)
        favoriteDate!!.text = getDepartureText(requireContext(), connection.departureDate)
        cardFavorites!!.visibility = View.VISIBLE
        latestFavorite = favoriteConnection
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        disposable?.dispose()
        disposable = favoriteDao!!.getLatestFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { favoriteConnection: FavoriteConnection ->
                    Log.d(TAG, "Got latest connection: $favoriteConnection")
                    bindConnection(favoriteConnection)
                }, errorConsumer)
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }

    private fun onOpenButtonPressed() {
        if (favoriteSelectedListener != null && latestFavorite != null) {
            favoriteSelectedListener!!.onFavoriteSelected(latestFavorite!!)
        } else if (BuildConfig.DEBUG) {
            Log.w(TAG, "mListener or mLatestFavorite is null ($favoriteSelectedListener, $latestFavorite)")
        }
    }

    private fun onMoreButtonPressed() {
        val intent = Intent(context, FavoritesActivity::class.java)
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        favoriteSelectedListener = if (context is OnFavoriteSelectedListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFavoriteSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        favoriteSelectedListener = null
    }

    override val errorMessage: Int
        get() = R.string.error_failed_to_load_favorites

    interface OnFavoriteSelectedListener {
        fun onFavoriteSelected(favoriteConnection: FavoriteConnection)
    }

    companion object {
        private const val TAG = "FavoriteCardFragment"
    }
}