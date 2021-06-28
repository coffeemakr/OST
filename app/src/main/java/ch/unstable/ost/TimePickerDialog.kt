package ch.unstable.ost

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import ch.unstable.ost.utils.TimeDateUtils.formatDate
import com.google.android.material.tabs.TabLayout
import java.util.*

class TimePickerDialog(context: Context, themeResId: Int, timeRestrictionType: TimeRestrictionType?, date: Date?, onTimeSelectedListener: OnTimeSelected?) : AlertDialog(context, themeResId), DialogInterface.OnClickListener {
    private val mOnTimeSelectedListener: OnTimeSelected?
    private val mDefaultTimeRestrictionType: TimeRestrictionType?
    private val calendar: Calendar
    private var hourPicker: NumberPicker? = null
    private var minutePicker: NumberPicker? = null
    private var dateButton: Button? = null
    private val mOnclickListener = View.OnClickListener { v: View ->
        when (v.id) {
            R.id.dateButton -> showDateDialog()
            R.id.resetTimeButton -> onResetTime()
        }
    }
    private var arrivalDepartureSwitcher: TabLayout? = null

    constructor(context: Context, timeRestrictionType: TimeRestrictionType?, date: Date?, onTimeSelectedListener: OnTimeSelected?) : this(context, 0, timeRestrictionType, date, onTimeSelectedListener) {}

    private fun updateDateView() {
        val date = formatDate(context, calendar.time)
        dateButton!!.text = date
    }

    private fun onViewCreated(view: View) {
        val arrivalDepartureSwitcher: TabLayout = view.findViewById(R.id.arrivalDepartureSwitcher)
        when (mDefaultTimeRestrictionType) {
            TimeRestrictionType.DEPARTURE -> arrivalDepartureSwitcher.getTabAt(0)!!.select()
            TimeRestrictionType.ARRIVAL -> arrivalDepartureSwitcher.getTabAt(1)!!.select()
        }
        this.arrivalDepartureSwitcher = arrivalDepartureSwitcher
        dateButton = view.findViewById(R.id.dateButton)
        dateButton?.setOnClickListener(mOnclickListener)

        updateDateView()
        hourPicker = view.findViewById(R.id.hourPicker)
        hourPicker?.minValue = 0
        hourPicker?.maxValue = 23
        hourPicker?.value = calendar[Calendar.HOUR_OF_DAY]
        minutePicker = view.findViewById(R.id.minutePicker)
        minutePicker?.minValue = 0
        minutePicker?.maxValue = 59
        minutePicker?.value = calendar[Calendar.MINUTE]
        minutePicker?.setOnValueChangedListener { _: NumberPicker?, oldVal: Int, newVal: Int -> calendar[Calendar.MINUTE] = newVal }
        hourPicker?.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int -> calendar[Calendar.HOUR_OF_DAY] = newVal }
        val resetTimeButton = view.findViewById<View>(R.id.resetTimeButton)
        resetTimeButton.setOnClickListener(mOnclickListener)
    }

    private fun onResetTime() {
        calendar.timeInMillis = System.currentTimeMillis()
        hourPicker!!.value = calendar[Calendar.HOUR_OF_DAY]
        minutePicker!!.value = calendar[Calendar.MINUTE]
        updateDateView()
    }

    private fun showDateDialog() {
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        DatePickerDialog(context, { datePicker: DatePicker, yearInt: Int, monthInt: Int, dayOfMonth: Int -> onDateSet(datePicker, yearInt, monthInt, dayOfMonth) }, year, month, day).show()
    }

    private fun onDateSet(datePicker: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        updateDateView()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            BUTTON_POSITIVE -> {
                val selectedTab = arrivalDepartureSwitcher!!.selectedTabPosition
                if (mOnTimeSelectedListener != null) {
                    when (selectedTab) {
                        0 -> mOnTimeSelectedListener.onDepartureTimeSelected(calendar.time)
                        1 -> mOnTimeSelectedListener.onArrivalTimeSelected(calendar.time)
                        else -> Log.e(TAG, "Invalid or no arrival/departure tab selected: $selectedTab")
                    }
                }
            }
            BUTTON_NEGATIVE -> cancel()
        }
    }

    enum class TimeRestrictionType {
        DEPARTURE, ARRIVAL
    }

    interface OnTimeSelected {
        fun onArrivalTimeSelected(date: Date)
        fun onDepartureTimeSelected(date: Date)
    }

    companion object {
        private val TAG = TimePickerDialog::class.java.simpleName
    }

    init {
        val themeContext = getContext()
        val inflater = LayoutInflater.from(themeContext)
        val view = inflater.inflate(R.layout.dialog_time_picker, null)
        setView(view)
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this)
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this)
        mOnTimeSelectedListener = onTimeSelectedListener
        calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        } else {
            calendar.timeInMillis = System.currentTimeMillis()
        }
        mDefaultTimeRestrictionType = timeRestrictionType
        onViewCreated(view)
    }
}