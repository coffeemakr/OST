package ch.unstable.ost.api.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;


class Connection implements ch.unstable.ost.api.model.Connection, Parcelable {

    @JsonAdapter(MinusDateDeserializer.class)
    private final Date arrival;
    @JsonAdapter(MinusDateDeserializer.class)
    private final Date departure;
    private final String from;
    private final String to;
    private final int duration;

    @SerializedName("legs")
    private final Section[] sections;

    Connection(Date arrival, Date departure, String from, String to, int duration, Section[] sections) {
        this.arrival = arrival;
        this.departure = departure;
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.sections = sections;
    }

    protected Connection(Parcel in) {
        arrival = ParcelUtils.readDate(in);
        departure = ParcelUtils.readDate(in);
        from = in.readString();
        to = in.readString();
        duration = in.readInt();
        sections = (Section[]) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
        dest.writeInt(duration);
        dest.writeSerializable(sections);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel in) {
            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };

    @Override
    public Section[] getSections() {
        return new Section[0];
    }

    @Override
    public Date getDepartureTime() {
        return departure;
    }

    @Override
    public Date getArrivalTime() {
        return arrival;
    }
}
