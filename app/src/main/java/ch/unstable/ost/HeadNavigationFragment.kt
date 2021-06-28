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
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import ch.unstable.ost.TimePickerDialog.OnTimeSelected
import ch.unstable.ost.TimePickerDialog.TimeRestrictionType
import ch.unstable.ost.api.model.ConnectionQuery
import ch.unstable.ost.databinding.FragmentHeadNavigationBinding
import ch.unstable.ost.utils.LocalizationUtils.getArrivalOrDepartureText
import com.google.common.base.Verify
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import java.util.*

class HeadNavigationFragment : BaseNavigationFragment() {
    private var onButtonClickListener: View.OnClickListener? = null
    private var selectionState: SelectionState? = null
    private var binding: FragmentHeadNavigationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onButtonClickListener = OnNavigationButtonsClickListener()
        selectionState = null
        if (savedInstanceState != null) {
            selectionState = savedInstanceState.getParcelable(KEY_STATE)
        } else if (arguments != null) {
            selectionState = requireArguments().getParcelable(KEY_STATE)
        }
        if (selectionState == null) {
            selectionState = SelectionState()
        }
        val disposable = selectionState!!.getChangeObservable()
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
        val binding = binding ?: return
        binding.timeView.text = getArrivalOrDepartureText(
                context,
                selectionState!!.getArrivalTime(),
                selectionState!!.getDepartureTime())
        binding.toButton.text = getToButtonText(context, selectionState!!.getTo())
        binding.fromButton.text = getFromButtonText(context, selectionState!!.getFrom())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (selectionState != null) {
            outState.putParcelable(KEY_STATE, selectionState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentHeadNavigationBinding.inflate(inflater, container, false)
        this.binding = binding
        binding.timeSettingsContainer.setOnClickListener(onButtonClickListener)
        binding.fromButton.setOnClickListener(onButtonClickListener)
        binding.toButton.setOnClickListener(onButtonClickListener)
        binding.reverseDirectionButton.setOnClickListener(onButtonClickListener)
        updateViews()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
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
                    selectionState!!.setFrom(name)
                }
                REQUEST_CODE_CHOOSE_TO -> {
                    name = data!!.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME)
                    Verify.verifyNotNull(name, "Choose station result (to) is null")
                    selectionState!!.setTo(name)
                }
                else -> {
                }
            }
        }
    }

    private fun onQueryChanged() {
        if (selectionState!!.getTo() != null && selectionState!!.getFrom() != null) {
            val query = selectionState!!.createQuery()
            selectRoute(query)
        }
    }

    private fun onReverseDirectionRequested() {
        val context = context
        val animation = AnimationUtils.loadAnimation(context, R.anim.half_rotation)
        binding?.reverseDirectionButton?.startAnimation(animation)
        val fadeInTop = AnimationUtils.loadAnimation(context, R.anim.fade_in_top)
        val fadeOutTop = AnimationUtils.loadAnimation(context, R.anim.fade_out_top)
        val fadeInBottom = AnimationUtils.loadAnimation(context, R.anim.fade_in_bottom)
        val fadeOutBottom = AnimationUtils.loadAnimation(context, R.anim.fade_out_bottom)
        val newTo = selectionState!!.getFrom()
        val newFrom = selectionState!!.getTo()
        fadeOutTop.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation1: Animation) {
                // Not interested in this event
            }

            override fun onAnimationEnd(animation1: Animation) {
                val binding = binding
                if(binding != null) {
                    binding.fromButton.text = getFromButtonText(context, newFrom)
                    binding.fromButton.startAnimation(fadeInTop)
                    binding.toButton.text = getToButtonText(context, newTo)
                    binding.toButton.startAnimation(fadeInBottom)
                }
                selectionState!!.setFrom(newFrom)
                selectionState!!.setTo(newTo)
            }

            override fun onAnimationRepeat(animation1: Animation) {
                // Not interested in this event
            }
        })
        binding?.fromButton?.startAnimation(fadeOutTop)
        binding?.toButton?.startAnimation(fadeOutBottom)
    }

    @MainThread
    fun updateQuery(query: ConnectionQuery?) {
        selectionState!!.setQuery(query!!)
    }

    fun clearQuery() {
        selectionState!!.setFrom(null)
        selectionState!!.setTo(null)
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
            var date = selectionState!!.getDepartureTime()
            var restrictionType = TimeRestrictionType.DEPARTURE
            if (date == null && selectionState!!.getArrivalTime() != null) {
                restrictionType = TimeRestrictionType.ARRIVAL
                date = selectionState!!.getArrivalTime()
            }
            val timePickerDialog = TimePickerDialog(context!!, restrictionType, date, object : OnTimeSelected {
                override fun onArrivalTimeSelected(date: Date) {
                    selectionState!!.setArrivalTime(date)
                }

                override fun onDepartureTimeSelected(date: Date) {
                    selectionState!!.setDepartureTime(date)
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