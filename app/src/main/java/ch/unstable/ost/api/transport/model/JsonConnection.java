package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;

import java.util.Arrays;
import java.util.Date;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.transport.types.DurationDeserializer;
import ch.unstable.ost.utils.ParcelUtils;

public class JsonConnection implements Parcelable, ch.unstable.ost.api.model.Connection {

    public static final Creator<JsonConnection> CREATOR = new Creator<JsonConnection>() {
        @Override
        public JsonConnection createFromParcel(Parcel in) {
            return new JsonConnection(in);
        }

        @Override
        public JsonConnection[] newArray(int size) {
            return new JsonConnection[size];
        }
    };

    private final Checkpoint from;
    private final Checkpoint to;
    @JsonAdapter(DurationDeserializer.class)
    private final Long duration;
    private final Section[] sections;

    protected JsonConnection(Parcel in) {
        this.from = ParcelUtils.readNullableParcelable(in, Checkpoint.CREATOR);
        this.to = ParcelUtils.readNullableParcelable(in, Checkpoint.CREATOR);
        this.duration = ParcelUtils.readNullableLong(in);
        this.sections = in.createTypedArray(Section.CREATOR);
    }

    public JsonConnection(Checkpoint from, Checkpoint to, Long duration, Section[] sections) {
        this.from = from;
        this.to = to;
        this.duration = duration;
        if(true || BuildConfig.DEBUG) {
            long expectedDuration = (getArrivalDate().getTime() - getDepartureDate().getTime()) / 60000;
            if(true || this.duration != expectedDuration) {
                throw new AssertionError("Duration is " + duration + " but expected " + expectedDuration );
            }
        }
        if (sections == null) {
            sections = new Section[0];
        }
        this.sections = sections;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeNullableParcelable(dest, from, flags);
        ParcelUtils.writeNullableParcelable(dest, to, flags);
        ParcelUtils.writeNullableLong(dest, duration);
        dest.writeTypedArray(sections, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Checkpoint getFrom() {
        return from;
    }

    public Checkpoint getTo() {
        return to;
    }

    public Long getDuration() {
        return duration;
    }
    /*
    private final String[] products;
    private final int ocupation1st;
    private final int ocupation2nd;
     */

    @Override
    public Section[] getSections() {
        return sections;
    }

    @Override
    public Date getDepartureDate() {
        return getFrom().getDepartureTime();
    }

    @Override
    public Date getArrivalDate() {
        return getTo().getArrival();
    }

    @Override
    public String toString() {
        return "Connection{" +
                "from=" + from +
                ", end=" + to +
                ", duration='" + duration + '\'' +
                ", sections=" + Arrays.toString(sections) +
                '}';
    }
}
