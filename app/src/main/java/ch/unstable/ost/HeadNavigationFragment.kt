package ch.unstable.ost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import ch.unstable.ost.ChooseStationActivity
import ch.unstable.ost.TimePickerDialog.OnTimeSelected
import ch.unstable.ost.TimePickerDialog.TimeRestrictionType
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.utils.LocalizationUtils.getArrivalOrDepartureText
import com.google.common.base.Verify
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import java.util.*

class HeadNavigationFragment : BaseNavigationFragment() {
    private var mOnButtonClickListener: View.OnClickListener? = null
    private var mSelectionState: SelectionState? = null
    private var mToButton: Button? = null
    private var mFromButton: Button? = null
    private var mReverseDirectionButton: ImageButton? = null
    private var mTime: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOnButtonClickListener = OnNavigationButtonsClickListener()
        mSelectionState = null
        if (savedInstanceState != null) {
            mSelectionState = savedInstanceState.getParcelable(KEY_STATE)
        } else if (arguments != null) {
            mSelectionState = requireArguments().getParcelable(KEY_STATE)
        }
        if (mSelectionState == null) {
            mSelectionState = SelectionState()
        }
        val disposable = mSelectionState!!.getChangeObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectionStateObserver)
    }

    private val selectionStateObserver: Consumer<SelectionState>
        private get() = Consumer {
            updateViews()
            onQueryChanged()
        }

    private fun updateViews() {
        val context = context ?: return
        mTime!!.text = getArrivalOrDepartureText(
                context,
                mSelectionState!!.getArrivalTime(),
                mSelectionState!!.getDepartureTime())
        mToButton!!.text = getToButtonText(context, mSelectionState!!.getTo())
        mFromButton!!.text = getFromButtonText(context, mSelectionState!!.getFrom())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mSelectionState != null) {
            outState.putParcelable(KEY_STATE, mSelectionState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_head_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFromButton = view.findViewById(R.id.fromButton)
        mToButton = view.findViewById(R.id.toButton)
        mReverseDirectionButton = view.findViewById(R.id.reverseDirectionButton)
        mTime = view.findViewById(R.id.timeView)
        val timeSettingsContainer = view.findViewById<View>(R.id.timeSettingsContainer)
        timeSettingsContainer.setOnClickListener(mOnButtonClickListener)
        mFromButton?.setOnClickListener(mOnButtonClickListener)
        mToButton?.setOnClickListener(mOnButtonClickListener)
        mReverseDirectionButton?.setOnClickListener(mOnButtonClickListener)
        updateViews()
    }

    private fun startStationChooser(@StringRes chooseRequest: Int, codeTo: Int) {
        val chooseString = requireContext().getString(chooseRequest)
        val intent = Intent(context, ChooseStationActivity::class.java)
        intent.action = Intent.ACTION_RUN
        intent.putExtra(ChooseStationActivity.EXTRA_CHOOSE_PROMPT, chooseString)
        startActivityForResult(intent, codeTo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val name: String
            when (requestCode) {
                REQUEST_CODE_CHOOSE_FROM -> {
                    name = data!!.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME)
                    Verify.verifyNotNull(name, "Choose station result (from) is null")
                    mSelectionState!!.setFrom(name)
                }
                REQUEST_CODE_CHOOSE_TO -> {
                    name = data!!.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME)
                    Verify.verifyNotNull(name, "Choose station result (to) is null")
                    mSelectionState!!.setTo(name)
                }
                else -> {
                }
            }
        }
    }

    private fun onQueryChanged() {
        if (mSelectionState!!.getTo() != null && mSelectionState!!.getFrom() != null) {
            val query = mSelectionState!!.createQuery()
            selectRoute(query)
        }
    }

    private fun onReverseDirectionRequested() {
        val context = context
        val animation = AnimationUtils.loadAnimation(context, R.anim.half_rotation)
        mReverseDirectionButton!!.startAnimation(animation)
        val fadeInTop = AnimationUtils.loadAnimation(context, R.anim.fade_in_top)
        val fadeOutTop = AnimationUtils.loadAnimation(context, R.anim.fade_out_top)
        val fadeInBottom = AnimationUtils.loadAnimation(context, R.anim.fade_in_bottom)
        val fadeOutBottom = AnimationUtils.loadAnimation(context, R.anim.fade_out_bottom)
        val newTo = mSelectionState!!.getFrom()
        val newFrom = mSelectionState!!.getTo()
        fadeOutTop.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation1: Animation) {
                // Not interested in this event
            }

            override fun onAnimationEnd(animation1: Animation) {
                mFromButton!!.text = getFromButtonText(context, newFrom)
                mFromButton!!.startAnimation(fadeInTop)
                mToButton!!.text = getToButtonText(context, newTo)
                mToButton!!.startAnimation(fadeInBottom)
                mSelectionState!!.setFrom(newFrom)
                mSelectionState!!.setTo(newTo)
            }

            override fun onAnimationRepeat(animation1: Animation) {
                // Not interested in this event
            }
        })
        mFromButton!!.startAnimation(fadeOutTop)
        mToButton!!.startAnimation(fadeOutBottom)
    }

    @MainThread
    fun updateQuery(query: ConnectionQuery?) {
        mSelectionState!!.setQuery(query!!)
    }

    fun clearQuery() {
        mSelectionState!!.setFrom(null)
        mSelectionState!!.setTo(null)
    }

    inner class OnNavigationButtonsClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.toButton -> startStationChooser(R.string.request_choose_to, REQUEST_CODE_CHOOSE_TO)
                R.id.fromButton -> startStationChooser(R.string.request_choose_from, REQUEST_CODE_CHOOSE_FROM)
                R.id.reverseDirectionButton -> onReverseDirectionRequested()
                R.id.timeSettingsContainer -> onOpenTimeSettings()
            }
        }

        private fun onOpenTimeSettings() {
            var date = mSelectionState!!.getDepartureTime()
            var restrictionType = TimeRestrictionType.DEPARTURE
            if (date == null && mSelectionState!!.getArrivalTime() != null) {
                restrictionType = TimeRestrictionType.ARRIVAL
                date = mSelectionState!!.getArrivalTime()
            }
            val timePickerDialog = TimePickerDialog(context!!, restrictionType, date, object : OnTimeSelected {
                override fun onArrivalTimeSelected(date: Date) {
                    mSelectionState!!.setArrivalTime(date)
                }

                override fun onDepartureTimeSelected(date: Date) {
                    mSelectionState!!.setDepartureTime(date)
                }
            })
            timePickerDialog.show()
        }
    }

    companion object {
        private const val REQUEST_CODE_CHOOSE_TO = 1
        private const val REQUEST_CODE_CHOOSE_FROM = 2
        private const val KEY_STATE = "KEY_STATE"
        private const val TAG = "HeadNavigationFragment"
        private fun getToButtonText(context: Context?, to: String?): String {
            return to ?: context!!.getString(R.string.request_choose_to)
        }

        private fun getFromButtonText(context: Context?, from: String?): String {
            return from ?: context!!.getString(R.string.request_choose_from)
        }
    }
}