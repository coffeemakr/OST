package ch.unstable.ost.api.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 * {
 *     "arrival" : "2016-10-27 19:25:00",
 *     "sbb_name" : "Wädenswil",
 *     "name" : "Wädenswil",
 *     "stopid" : "8503206",
 *     "waittime" : 240
 * }
 * </pre>
 */
public class SectionExit implements Parcelable {
    public static final Creator<SectionExit> CREATOR = new Creator<SectionExit>() {
        @Override
        public SectionExit createFromParcel(Parcel in) {
            return new SectionExit(in);
        }

        @Override
        public SectionExit[] newArray(int size) {
            return new SectionExit[size];
        }
    };
    @SerializedName("arrival")
    private final String arrival;
    @SerializedName("sbb_name")
    private final String sbbName;
    @SerializedName("stopid")
    private final String stopId;
    @SerializedName("waittime")
    private final int waitTime;

    public SectionExit(String arrival, String sbbName, String stopId, int waitTime) {
        this.arrival = arrival;
        this.sbbName = sbbName;
        this.stopId = stopId;
        this.waitTime = waitTime;
    }

    protected SectionExit(Parcel in) {
        arrival = in.readString();
        sbbName = in.readString();
        stopId = in.readString();
        waitTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(arrival);
        dest.writeString(sbbName);
        dest.writeString(stopId);
        dest.writeInt(waitTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSbbName() {
        return sbbName;
    }

    public String getArrival() {
        return arrival;
    }

    public String getStopId() {
        return stopId;
    }

    public int getWaitTime() {
        return waitTime;
    }
}
