package ch.unstable.ost;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Date;

import ch.unstable.ost.utils.TimeDateUtils;


public class TimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private static final String TAG = TimePickerDialog.class.getSimpleName();
    @Nullable
    private final OnTimeSelected mOnTimeSelectedListener;
    private final TimeRestrictionType mDefaultTimeRestrictionType;
    private final Calendar calendar;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Button dateButton;
    private final View.OnClickListener mOnclickListener = v -> {
        switch (v.getId()) {
            case R.id.dateButton:
                showDateDialog();
                break;
            case R.id.resetTimeButton:
                onResetTime();
                break;
        }
    };
    private TabLayout arrivalDepartureSwitcher;

    public TimePickerDialog(@NonNull Context context, TimeRestrictionType timeRestrictionType, @Nullable Date date, @Nullable OnTimeSelected onTimeSelectedListener) {
        this(context, 0, timeRestrictionType, date, onTimeSelectedListener);
    }

    public TimePickerDialog(@NonNull Context context, int themeResId, TimeRestrictionType timeRestrictionType, @Nullable Date date, @Nullable OnTimeSelected onTimeSelectedListener) {
        super(context, themeResId);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.dialog_time_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        this.mOnTimeSelectedListener = onTimeSelectedListener;
        calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
        mDefaultTimeRestrictionType = timeRestrictionType;
        onViewCreated(view);
    }

    private void updateDateView() {
        String date = TimeDateUtils.formatDate(getContext(), calendar.getTime());
        dateButton.setText(date);
    }


    private void onViewCreated(View view) {

        arrivalDepartureSwitcher = view.findViewById(R.id.arrivalDepartureSwitcher);
        switch (mDefaultTimeRestrictionType) {
            case DEPARTURE:
                arrivalDepartureSwitcher.getTabAt(0).select();
                break;
            case ARRIVAL:
                arrivalDepartureSwitcher.getTabAt(1).select();
                break;
        }
        dateButton = view.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(mOnclickListener);
        updateDateView();
        hourPicker = view.findViewById(R.id.hourPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));

        minutePicker = view.findViewById(R.id.minutePicker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> calendar.set(Calendar.MINUTE, newVal));
        hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> calendar.set(Calendar.HOUR_OF_DAY, newVal));
        View resetTimeButton = view.findViewById(R.id.resetTimeButton);
        resetTimeButton.setOnClickListener(mOnclickListener);
    }

    private void onResetTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        updateDateView();
    }

    private void showDateDialog() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        new DatePickerDialog(getContext(), this::onDateSet, year, month, day).show();
    }

    private void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateView();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                int selectedTab = arrivalDepartureSwitcher.getSelectedTabPosition();
                if (mOnTimeSelectedListener != null) {
                    switch (selectedTab) {
                        case 0:
                            mOnTimeSelectedListener.onDepartureTimeSelected(calendar.getTime());
                            break;
                        case 1:
                            mOnTimeSelectedListener.onArrivalTimeSelected(calendar.getTime());
                            break;
                        default:
                            Log.e(TAG, "Invalid or no arrival/departure tab selected: " + selectedTab);
                    }
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    public enum TimeRestrictionType {
        DEPARTURE, ARRIVAL
    }

    public interface OnTimeSelected {
        void onArrivalTimeSelected(@NonNull Date date);

        void onDepartureTimeSelected(@NonNull Date date);
    }
}
