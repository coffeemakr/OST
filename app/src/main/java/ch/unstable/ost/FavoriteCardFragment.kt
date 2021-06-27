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
    private var mListener: OnFavoriteSelectedListener? = null
    private var mCardFavorites: View? = null
    private var mFavoriteFromTo: TextView? = null
    private var mFavoriteDate: TextView? = null
    private var mFavoriteDao: FavoriteConnectionDao? = null
    private var mLatestFavorite: FavoriteConnection? = null
    private var mDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFavoriteDao = Databases.getCacheDatabase(context).favoriteConnectionDao()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCardFavorites = view.findViewById(R.id.cardFavorites)
        mCardFavorites?.visibility = View.GONE
        mFavoriteFromTo = view.findViewById(R.id.favoriteFromTo)
        mFavoriteDate = view.findViewById(R.id.favoriteDate)
        val moreButton = view.findViewById<View>(R.id.buttonMoreFavorites)
        moreButton.setOnClickListener { onMoreButtonPressed() }
        val openButton = view.findViewById<View>(R.id.buttonOpenFavorite)
        openButton.setOnClickListener { onOpenButtonPressed() }
    }

    private fun bindConnection(favoriteConnection: FavoriteConnection) {
        val connection = favoriteConnection.connection
        bindFromToText(mFavoriteFromTo!!, connection)
        mFavoriteDate!!.text = getDepartureText(requireContext(), connection.departureDate)
        mCardFavorites!!.visibility = View.VISIBLE
        mLatestFavorite = favoriteConnection
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        if (mDisposable != null) mDisposable!!.dispose()
        mDisposable = mFavoriteDao!!.latestFavorite
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { favoriteConnection: FavoriteConnection ->
                    Log.d(TAG, "Got latest connection: $favoriteConnection")
                    bindConnection(favoriteConnection)
                }, errorConsumer)
    }

    override fun onStop() {
        super.onStop()
        mDisposable!!.dispose()
    }

    private fun onOpenButtonPressed() {
        if (mListener != null && mLatestFavorite != null) {
            mListener!!.onFavoriteSelected(mLatestFavorite!!)
        } else if (BuildConfig.DEBUG) {
            Log.w(TAG, "mListener or mLatestFavorite is null ($mListener, $mLatestFavorite)")
        }
    }

    private fun onMoreButtonPressed() {
        val intent = Intent(context, FavoritesActivity::class.java)
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFavoriteSelectedListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFavoriteSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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