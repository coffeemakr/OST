package ch.unstable.ost;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import java.util.Calendar;
import java.util.Date;

import ch.unstable.ost.utils.TimeDateUtils;


public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_RESULT_ARRIVAL_TIME = "TimePickerFragment.EXTRA_RESULT_ARRIVAL_TIME";
    public static final String EXTRA_RESULT_DEPARTURE_TIME = "TimePickerFragment.EXTRA_RESULT_DEPARTURE_TIME";
    private static final String TAG = "TimePickerFragment";
    private static final String KEY_TIME = "TimePickerFragment.KEY_TIME";
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Calendar calendar;
    private Button dateButton;


    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_TIME, date.getTime());
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }


    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, 0);

        Date date = null;
        if(savedInstanceState != null) {
            long dateTime = savedInstanceState.getLong(KEY_TIME, -1);
            if(dateTime > 0) {
                date = new Date(dateTime);
            }
        } else if(getArguments() != null){
            long dateTime = getArguments().getLong(KEY_TIME, -1);
            if(dateTime > 0) {
                date = new Date(dateTime);
            }
        }

        calendar = Calendar.getInstance();
        if(date != null) {
            calendar.setTime(date);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_TIME, calendar.getTimeInMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_picker, container, false);
    }

    private void updateDateView() {
        String date = TimeDateUtils.formatDate(getContext(), calendar.getTime());
        dateButton.setText(date);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dateButton = (Button) view.findViewById(R.id.dateButton);
        dateButton.setOnClickListener(mOnclickListener);
        updateDateView();
        hourPicker = (NumberPicker) view.findViewById(R.id.hourPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minutePicker = (NumberPicker) view.findViewById(R.id.minutePicker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar.set(Calendar.MINUTE, newVal);
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "Minute changed: " + DateFormat.getDateFormat(getContext()).format(calendar.getTime()));
                }
            }
        });

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar.set(Calendar.HOUR_OF_DAY, newVal);
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "Hour changed: " + DateFormat.getDateFormat(getContext()).format(calendar.getTime()));
                }
            }
        });
        Button okButton = (Button) view.findViewById(R.id.okButton);
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        View resetTimeButton = view.findViewById(R.id.resetTimeButton);
        okButton.setOnClickListener(mOnclickListener);
        cancelButton.setOnClickListener(mOnclickListener);
        resetTimeButton.setOnClickListener(mOnclickListener);
    }

    private final View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.okButton:
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_RESULT_DEPARTURE_TIME, calendar.getTime().getTime());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                    break;
                case R.id.cancelButton:
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
                    dismiss();
                    break;
                case R.id.dateButton:
                    showDateDialog();
                    break;
                case R.id.resetTimeButton:
                    onResetTime();
                    break;
            }
        }
    };

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
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateView();
            }
        }, year, month, day).show();
    }
}
