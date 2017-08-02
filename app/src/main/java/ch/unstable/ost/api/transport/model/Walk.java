package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;

import ch.unstable.ost.api.transport.types.DurationDeserializer;

class Walk implements Parcelable {
    public static final Creator<Walk> CREATOR = new Creator<Walk>() {
        @Override
        public Walk createFromParcel(Parcel in) {
            return new Walk(in);
        }

        @Override
        public Walk[] newArray(int size) {
            return new Walk[size];
        }
    };
    @JsonAdapter(DurationDeserializer.class)
    private final long duration;

    public Walk(Long duration) {
        if (duration == null) {
            throw new NullPointerException("duration is null");
        }
        this.duration = duration;
    }

    protected Walk(Parcel in) {
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getDuration() {
        return duration;
    }
}
