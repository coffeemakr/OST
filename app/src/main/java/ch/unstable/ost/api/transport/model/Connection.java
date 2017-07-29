package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;

import java.util.Arrays;

import ch.unstable.ost.api.transport.types.DurationDeserializer;
import ch.unstable.ost.utils.ParcelUtils;

public class Connection implements Parcelable {

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

    private final Checkpoint from;
    private final Checkpoint to;
    @JsonAdapter(DurationDeserializer.class)
    private final Long duration;
    private final Section[] sections;

    protected Connection(Parcel in) {
        this.from = ParcelUtils.readNullableParcelable(in, Checkpoint.CREATOR);
        this.to = ParcelUtils.readNullableParcelable(in, Checkpoint.CREATOR);
        this.duration = ParcelUtils.readNullableLong(in);
        this.sections = in.createTypedArray(Section.CREATOR);
    }

    public Connection(Checkpoint from, Checkpoint to, Long duration, Section[] sections) {
        this.from = from;
        this.to = to;
        this.duration = duration;
        if(sections == null) {
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

    public Section[] getSections() {
        return sections;
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
