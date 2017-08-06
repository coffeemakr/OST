package ch.unstable.ost.api.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.utils.ParcelUtils;

public class ConnectionQuery implements Parcelable {
    public static final Parcelable.Creator<ConnectionQuery> CREATOR
            = new Parcelable.Creator<ConnectionQuery>() {
        public ConnectionQuery createFromParcel(Parcel in) {
            return new ConnectionQuery(in);
        }

        public ConnectionQuery[] newArray(int size) {
            return new ConnectionQuery[size];
        }
    };
    @NonNull
    private final String from;
    @NonNull
    private final String to;
    private final String[] via;
    @Nullable
    private final Date departureTime;
    @Nullable
    private final Date arrivalTime;

    private ConnectionQuery(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.via = builder.via.toArray(new String[builder.via.size()]);
        this.departureTime = builder.departureTime;
        this.arrivalTime = builder.arrivalTime;
    }

    private ConnectionQuery(Parcel in) {
        this.from = in.readString();
        this.to = in.readString();
        this.via = in.createStringArray();
        this.departureTime = ParcelUtils.readDate(in);
        this.arrivalTime = ParcelUtils.readDate(in);
    }

    @NonNull
    public String getFrom() {
        return from;
    }

    @NonNull
    public String getTo() {
        return to;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(from);
        out.writeString(to);
        out.writeStringArray(via);
        ParcelUtils.writeDate(out, departureTime);
        ParcelUtils.writeDate(out, arrivalTime);
    }

    public String[] getVia() {
        return via;
    }

    public boolean hasVia() {
        return via.length == 0;
    }

    @Nullable
    public Date getDepartureTime() {
        return departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionQuery that = (ConnectionQuery) o;
        return Objects.equal(from, that.from) &&
                Objects.equal(to, that.to) &&
                Objects.equal(via, that.via) &&
                Objects.equal(departureTime, that.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from, to, via, departureTime);
    }


    @SuppressWarnings("UnusedReturnValue")
    public static class Builder implements Parcelable {

        private static final int RESTRICTION_TYPE_ARRIVAL = 1;
        private static final int RESTRICTION_TYPE_DEPARTURE = 0;


        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
        @NonNull
        private List<String> via = new ArrayList<>();
        private String from = null;
        private String to = null;
        private Date departureTime;
        private Date arrivalTime;

        public Builder() {

        }

        public Builder(ConnectionQuery connectionQuery) {
            this.via = Arrays.asList(connectionQuery.via);
            this.from = connectionQuery.from;
            this.to = connectionQuery.to;
            this.departureTime = connectionQuery.departureTime;
            this.arrivalTime = connectionQuery.arrivalTime;
        }

        protected Builder(Parcel in) {
            via = in.createStringArrayList();
            from = in.readString();
            to = in.readString();
            departureTime = ParcelUtils.readDate(in);
            arrivalTime = ParcelUtils.readDate(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(via);
            dest.writeString(from);
            dest.writeString(to);
            ParcelUtils.writeDate(dest, departureTime);
            ParcelUtils.writeDate(dest, arrivalTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @NonNull
        public List<String> getVia() {
            return via;
        }

        public Builder setVia(String... via) {
            if (via == null || via.length == 1 && via[0] == null) {
                this.via.clear();
            } else {
                this.via = Arrays.asList(via);
            }
            return this;
        }

        public Builder addVia(String via) {
            this.via.add(via);
            return this;
        }

        public String getFrom() {
            return from;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public String getTo() {
            return to;
        }

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public ConnectionQuery build() {
            if (from == null) throw new IllegalStateException("from is null");
            if (to == null) throw new IllegalStateException("to is null");
            if (from.isEmpty()) throw new IllegalStateException("from is empty");
            if (to.isEmpty()) throw new IllegalStateException("to is empty");
            return new ConnectionQuery(this);
        }

        public Builder setDepartureTime(@Nullable Date departureTime) {
            this.departureTime = departureTime;
            this.arrivalTime = null;
            return this;
        }

        public Builder setArrivalTime(@Nullable Date arrivalTime) {
            this.departureTime = null;
            this.arrivalTime = arrivalTime;
            return this;
        }

        public Builder reverseDirection() {
            String temp = to;
            to = from;
            from = temp;
            return this;
        }

        @Nullable
        public Date getDepartureTime() {
            return departureTime;
        }

        public Date getArrivalTime() {
            return arrivalTime;
        }
    }
}
