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
    private final Date starTime;

    private ConnectionQuery(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.via = builder.via.toArray(new String[builder.via.size()]);
        this.starTime = builder.startTime;
    }

    private ConnectionQuery(Parcel in) {
        this.from = in.readString();
        this.to = in.readString();
        this.via = in.createStringArray();
        long timestamp = in.readLong();
        if (timestamp > 0) {
            this.starTime = new Date(in.readLong());
        } else {
            this.starTime = null;
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
        if (starTime != null) {
            out.writeLong(starTime.getTime());
        } else {
            out.writeLong(0);
        }
    }

    public String[] getVia() {
        return via;
    }

    public boolean hasVia() {
        return via.length == 0;
    }

    public boolean hasStartTime() {
        return starTime != null;
    }

    @Nullable
    public Date getStarTime() {
        return starTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionQuery that = (ConnectionQuery) o;
        return Objects.equal(from, that.from) &&
                Objects.equal(to, that.to) &&
                Objects.equal(via, that.via) &&
                Objects.equal(starTime, that.starTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from, to, via, starTime);
    }


    @SuppressWarnings("UnusedReturnValue")
    public static class Builder implements Parcelable {
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
        private Date startTime;

        public Builder() {

        }

        public Builder(ConnectionQuery connectionQuery) {
            this.via = Arrays.asList(connectionQuery.via);
            this.from = connectionQuery.from;
            this.to = connectionQuery.to;
            this.startTime = connectionQuery.starTime;
        }

        protected Builder(Parcel in) {
            via = in.createStringArrayList();
            from = in.readString();
            to = in.readString();
            startTime = ParcelUtils.readDate(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(via);
            dest.writeString(from);
            dest.writeString(to);
            ParcelUtils.writeDate(dest, startTime);
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

        public Builder setStartTime(@Nullable Date startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder reverseDirection() {
            String temp = to;
            to = from;
            from = temp;
            return this;
        }
    }

}
