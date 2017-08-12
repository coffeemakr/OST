package ch.unstable.ost;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.utils.ParcelUtils;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class SelectionState implements Parcelable {

    public static final Creator<SelectionState> CREATOR = new Creator<SelectionState>() {
        @Override
        public SelectionState createFromParcel(Parcel in) {
            return new SelectionState(in);
        }

        @Override
        public SelectionState[] newArray(int size) {
            return new SelectionState[size];
        }
    };
    private final PublishSubject<SelectionState> changeObservable = PublishSubject.create();
    @NonNull
    private List<String> via = new ArrayList<>();
    private String from = null;
    private String to = null;
    private Date departureTime;
    private Date arrivalTime;

    private SelectionState(Parcel in) {
        via = in.createStringArrayList();
        from = in.readString();
        to = in.readString();
        departureTime = ParcelUtils.readDate(in);
        arrivalTime = ParcelUtils.readDate(in);
    }

    public SelectionState() {
    }

    @NonNull
    static ConnectionQuery createConnectionQuery(SelectionState mSelectionState) {
        ConnectionQuery.Builder queryBuilder = new ConnectionQuery.Builder()
                .setTo(mSelectionState.to)
                .setFrom(mSelectionState.from);
        if (mSelectionState.arrivalTime != null) {
            queryBuilder.setArrivalTime(mSelectionState.arrivalTime);
        } else if (mSelectionState.departureTime != null) {
            queryBuilder.setDepartureTime(mSelectionState.departureTime);
        }
        if (!mSelectionState.via.isEmpty()) {
            queryBuilder.setVia(mSelectionState.via);
        }
        return queryBuilder.build();
    }

    public Observable<SelectionState> getChangeObservable() {
        return changeObservable;
    }

    private void notifyChanged() {
        changeObservable.onNext(this);
    }

    void setQuery(ConnectionQuery query) {
        via = Arrays.asList(query.getVia());
        from = query.getFrom();
        to = query.getTo();
        departureTime = query.getDepartureTime();
        arrivalTime = query.getArrivalTime();
        notifyChanged();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(getVia());
        dest.writeString(getFrom());
        dest.writeString(getTo());
        ParcelUtils.writeDate(dest, getDepartureTime());
        ParcelUtils.writeDate(dest, getArrivalTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    List<String> getVia() {
        return via;
    }

    void setVia(@NonNull List<String> via) {
        this.via = new ArrayList<>(via);
        notifyChanged();
    }

    String getFrom() {
        return from;
    }

    void setFrom(String from) {
        if(!Objects.equal(this.from, from)) {
            this.from = from;
            notifyChanged();
        }
    }

    String getTo() {
        return to;
    }

    void setTo(String to) {
        if(!Objects.equal(this.to, to)) {
            this.to = to;
            notifyChanged();
        }
    }

    Date getDepartureTime() {
        return departureTime;
    }

    void setDepartureTime(@Nullable Date departureTime) {
        this.departureTime = departureTime;
        this.arrivalTime = null;
        notifyChanged();
    }

    Date getArrivalTime() {
        return arrivalTime;
    }

    void setArrivalTime(@Nullable Date arrivalTime) {
        this.departureTime = null;
        this.arrivalTime = arrivalTime;
        notifyChanged();
    }
}
