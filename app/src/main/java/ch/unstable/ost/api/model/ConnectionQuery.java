package ch.unstable.ost.api.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.unstable.ost.utils.ParcelUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectionQuery implements Parcelable {
    public static final Parcelable.Creator<ConnectionQuery> CREATOR = new Parcelable.Creator<ConnectionQuery>() {

        @Override
        public ConnectionQuery createFromParcel(Parcel in) {
            return new ConnectionQuery(in);
        }

        @Override
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

    /**
     * Public constructor for POJO mapping
     * @param from
     * @param to
     * @param via
     * @param departureTime
     * @param arrivalTime
     *
     * @see Builder
     */
    public ConnectionQuery(String from, String to, @Nullable String[] via, @Nullable Date departureTime, @Nullable Date arrivalTime) {
        this.from = checkNotNull(from, "from is null");
        this.to = checkNotNull(to, "to is null");
        //checkArgument(departureTime != null || arrivalTime != null, "arrival or departure time need to be set");
        checkArgument(arrivalTime == null || departureTime == null, "only one of departure or arrival time can be set");
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        if (via == null) {
            this.via = new String[0];
        } else {
            this.via = Arrays.copyOf(via, via.length);
        }
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
        return via.length > 0;
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
                Arrays.equals(via, that.via) &&
                Objects.equal(departureTime, that.departureTime) &&
                Objects.equal(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from, to, Arrays.hashCode(via), departureTime, arrivalTime);
    }

    @Nullable
    public Date getArrivalTime() {
        return arrivalTime;
    }


    public static class Builder {

        @NonNull
        private List<String> via = new ArrayList<>();
        private String from = null;
        private String to = null;
        private Date departureTime;
        private Date arrivalTime;

        public Builder() {
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder setVia(@Nullable String... via) {
            if (via == null || (via.length == 1 && via[0] == null)) {
                this.via.clear();
            } else {
                this.via = new ArrayList<>(Arrays.asList(via));
            }
            return this;
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder addVia(String via) {
            this.via.add(via);
            return this;
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder setFrom(String from) {
            //noinspection ResultOfMethodCallIgnored
            checkNotNull(from, "from is null");
            Preconditions.checkArgument(!from.isEmpty(), "from may not be empty");
            this.from = from;
            return this;
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder setTo(String to) {
            //noinspection ResultOfMethodCallIgnored
            checkNotNull(to, "to is null");
            Preconditions.checkArgument(!to.isEmpty(), "to may not be empty");
            this.to = to;
            return this;
        }

        @NonNull
        public ConnectionQuery build() {
            Preconditions.checkState(from != null, "from is null");
            Preconditions.checkState(to != null, "to is null");
            return new ConnectionQuery(this);
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder reverseDirection() {
            String temp = to;
            to = from;
            from = temp;
            return this;
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder setDepartureTime(@Nullable Date departureTime) {
            this.departureTime = departureTime;
            this.arrivalTime = null;
            return this;
        }

        @NonNull
        @CanIgnoreReturnValue
        public Builder setArrivalTime(@Nullable Date arrivalTime) {
            this.departureTime = null;
            this.arrivalTime = arrivalTime;
            return this;
        }

        public void setVia(List<String> vias) {
            this.via = new ArrayList<>(vias);
        }
    }
}
