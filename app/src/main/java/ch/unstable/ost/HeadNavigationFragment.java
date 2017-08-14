package ch.unstable.ost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Verify;

import java.util.Calendar;
import java.util.Date;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.utils.TimeDateUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verifyNotNull;


public class HeadNavigationFragment extends BaseNavigationFragment {


    private static final int REQUEST_CODE_CHOOSE_TO = 1;
    private static final int REQUEST_CODE_CHOOSE_FROM = 2;
    private static final String KEY_STATE = "KEY_STATE";
    private static final String TAG = "HeadNavigationFragment";
    private View.OnClickListener mOnButtonClickListener;
    private SelectionState mSelectionState;
    private Button mToButton;
    private Button mFromButton;
    private ImageButton mReverseDirectionButton;
    private TextView mTime;

    public HeadNavigationFragment() {
        // Required empty public constructor
    }

    private static boolean isSameDay(Date first, Date second) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(first);
        cal2.setTime(second);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mOnButtonClickListener = new OnNavigationButtonsClickListener();

        mSelectionState = null;
        if (savedInstanceState != null) {
            mSelectionState = savedInstanceState.getParcelable(KEY_STATE);
        } else if (getArguments() != null) {
            mSelectionState = getArguments().getParcelable(KEY_STATE);
        }
        if (mSelectionState == null) {
            mSelectionState = new SelectionState();
        }
        mSelectionState.getChangeObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSelectionStateObserver());
    }

    @NonNull
    private Consumer<SelectionState> getSelectionStateObserver() {
        return new Consumer<SelectionState>() {
            @Override
            public void accept(SelectionState selectionState) throws Exception {
                updateViews();
                onQueryChanged();
            }
        };
    }

    private void updateViews() {
        final Context context = getContext();
        if(context == null) return;
        mTime.setText(getTimeString(context, mSelectionState));
        mToButton.setText(getToButtonText(context, mSelectionState.getTo()));
        mFromButton.setText(getFromButtonText(context, mSelectionState.getFrom()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSelectionState != null) {
            outState.putParcelable(KEY_STATE, mSelectionState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_head_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFromButton = (Button) view.findViewById(R.id.fromButton);
        mToButton = (Button) view.findViewById(R.id.toButton);
        mReverseDirectionButton = (ImageButton) view.findViewById(R.id.reverseDirectionButton);
        mTime = (TextView) view.findViewById(R.id.timeView);
        View timeSettingsContainer = view.findViewById(R.id.timeSettingsContainer);
        timeSettingsContainer.setOnClickListener(mOnButtonClickListener);
        mFromButton.setOnClickListener(mOnButtonClickListener);
        mToButton.setOnClickListener(mOnButtonClickListener);
        mReverseDirectionButton.setOnClickListener(mOnButtonClickListener);
        updateViews();
    }

    private void startStationChooser(@StringRes int chooseRequest, int codeTo) {
        String chooseString = getContext().getString(chooseRequest);
        Intent intent = new Intent(getContext(), ChooseStationActivity.class);
        intent.setAction(Intent.ACTION_RUN);
        intent.putExtra(ChooseStationActivity.EXTRA_CHOOSE_PROMPT, chooseString);
        startActivityForResult(intent, codeTo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String name;
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_FROM:
                    name = data.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME);
                    verifyNotNull(name, "Choose station result (from) is null");
                    mSelectionState.setFrom(name);
                    break;
                case REQUEST_CODE_CHOOSE_TO:
                    name = data.getStringExtra(ChooseStationActivity.EXTRA_RESULT_STATION_NAME);
                    verifyNotNull(name, "Choose station result (to) is null");
                    mSelectionState.setTo(name);
                    break;
                default:
                    // Unknown result
            }
        }
    }

    @NonNull
    private static String getTimeString(Context context, Date date, @StringRes int sameDayFormat, @StringRes int otherDayFormat) {
        Date today = new Date();
        if (isSameDay(today, date)) {
            return context.getString(sameDayFormat, TimeDateUtils.formatTime(date));
        } else {
            return context.getString(otherDayFormat, TimeDateUtils.formatTime(date), TimeDateUtils.formatDate(context, date));
        }
    }

    @NonNull
    private static String getTimeString(Context context, SelectionState selectionState) {
        checkNotNull(context, "context is null");
        checkNotNull(context, "selectionState is null");
        Date time;
        if ((time = selectionState.getDepartureTime()) != null) {
            return getTimeString(context, time, R.string.departure_time_same_day, R.string.departure_time_other_day);
        } else if ((time = selectionState.getArrivalTime()) != null) {
            return getTimeString(context, time, R.string.arrival_time_same_day, R.string.arrival_time_other_day);
        } else {
            return context.getString(R.string.departure_time_now);
        }
    }

    @NonNull
    private static String getToButtonText(Context context, @Nullable String to) {
        if (to == null) {
            return context.getString(R.string.request_choose_to);
        }
        return to;
    }

    @NonNull
    private static String getFromButtonText(Context context, @Nullable String from) {
        if (from == null) {
            return context.getString(R.string.request_choose_from);
        }
        return from;
    }

    private void onQueryChanged() {
        if (mSelectionState.getTo() != null && mSelectionState.getFrom() != null) {
            ConnectionQuery query = SelectionState.createConnectionQuery(mSelectionState);
            selectRoute(query);
        }
    }

    private void onReverseDirectionRequested() {
        final Context context = getContext();
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.half_rotation);
        mReverseDirectionButton.startAnimation(animation);
        final Animation fadeInTop = AnimationUtils.loadAnimation(context, R.anim.fade_in_top);
        final Animation fadeOutTop = AnimationUtils.loadAnimation(context, R.anim.fade_out_top);
        final Animation fadeInBottom = AnimationUtils.loadAnimation(context, R.anim.fade_in_bottom);
        final Animation fadeOutBottom = AnimationUtils.loadAnimation(context, R.anim.fade_out_bottom);
        final String newTo = mSelectionState.getFrom();
        final String newFrom = mSelectionState.getTo();
        fadeOutTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation1) {
                // Not interested in this event
            }

            @Override
            public void onAnimationEnd(Animation animation1) {
                mFromButton.setText(getFromButtonText(context, newFrom));
                mFromButton.startAnimation(fadeInTop);
                mToButton.setText(getToButtonText(context, newTo));
                mToButton.startAnimation(fadeInBottom);
                mSelectionState.setFrom(newFrom);
                mSelectionState.setTo(newTo);
            }

            @Override
            public void onAnimationRepeat(Animation animation1) {
                // Not interested in this event
            }
        });
        mFromButton.startAnimation(fadeOutTop);
        mToButton.startAnimation(fadeOutBottom);
    }

    @MainThread
    public void updateQuery(ConnectionQuery query) {
        mSelectionState.setQuery(query);
    }

    public void clearQuery() {
        mSelectionState.setFrom(null);
        mSelectionState.setTo(null);
    }

    public class OnNavigationButtonsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toButton:
                    startStationChooser(R.string.request_choose_to, REQUEST_CODE_CHOOSE_TO);
                    break;
                case R.id.fromButton:
                    startStationChooser(R.string.request_choose_from, REQUEST_CODE_CHOOSE_FROM);
                    break;
                case R.id.reverseDirectionButton:
                    onReverseDirectionRequested();
                    break;
                case R.id.timeSettingsContainer:
                    onOpenTimeSettings();
                    break;
            }
        }

        private void onOpenTimeSettings() {

            Date date = mSelectionState.getDepartureTime();
            TimePickerDialog.TimeRestrictionType restrictionType = TimePickerDialog.TimeRestrictionType.DEPARTURE;
            if (date == null && mSelectionState.getArrivalTime() != null) {
                restrictionType = TimePickerDialog.TimeRestrictionType.ARRIVAL;
                date = mSelectionState.getArrivalTime();
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), restrictionType, date, new TimePickerDialog.OnTimeSelected() {
                @Override
                public void onArrivalTimeSelected(@NonNull Date date) {
                    mSelectionState.setArrivalTime(date);
                }

                @Override
                public void onDepartureTimeSelected(@NonNull Date date) {
                    mSelectionState.setDepartureTime(date);
                }
            });
            timePickerDialog.show();
        }
    }
}
