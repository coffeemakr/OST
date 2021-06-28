package ch.unstable.ost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.database.Databases
import ch.unstable.ost.database.dao.QueryHistoryDao
import ch.unstable.ost.database.model.QueryHistory
import ch.unstable.ost.databinding.CardLastQueryBinding
import ch.unstable.ost.views.lists.query.QueryBinder.bindQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class LastQueryCardFragment : QuickstartCardFragment() {
    private var binding: CardLastQueryBinding? = null
    private lateinit var queryDao: QueryHistoryDao
    private var onQuerySelectedListener: OnQuerySelectedListener? = null
    private var lastQuery: QueryHistory? = null
    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryDao = Databases.getCacheDatabase(requireContext()).queryHistoryDao()
        compositeDisposable = CompositeDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onQuerySelectedListener = context as OnQuerySelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        onQuerySelectedListener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = CardLastQueryBinding.inflate(inflater, container, false)
        this.binding = binding
        binding.cardLastQuery.visibility = View.GONE
        binding.buttonMore.setOnClickListener { onShowMoreQueries() }
        binding.buttonOpen.setOnClickListener { onOpenQuery() }
        return binding.root

    }

    private fun onOpenQuery() {
        if (onQuerySelectedListener == null) {
            Log.w(TAG, "mOnQuerySelectedListener is null")
            return
        }
        lastQuery ?: error("mLastQuery is null")
        onQuerySelectedListener!!.onRouteSelected(lastQuery!!.query)
    }

    private fun onShowMoreQueries() {
        val intent = Intent(context, QueryHistoryActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val disposable = queryDao.latestQuery
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { queryHistory: QueryHistory -> updateLatestQuery(queryHistory) }, errorConsumer)
        compositeDisposable?.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.dispose()
    }

    @MainThread
    private fun updateLatestQuery(queryHistory: QueryHistory) {
        lastQuery = queryHistory
        val binding = this.binding
        if (binding != null) {
            binding.cardLastQuery.visibility = View.VISIBLE // TODO animate
            bindQuery(queryHistory, binding.lastQueryDate, binding.lastQueryFromTo)
        }
    }

    override val errorMessage: Int
        get() = R.string.error_failed_to_load_last_query

    interface OnQuerySelectedListener {
        @MainThread
        fun onRouteSelected(query: ConnectionQuery)
    }

    companion object {
        private const val TAG = "LastQueryCardFragment"
    }
}