package ch.unstable.ost.api.search.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import ch.unstable.ost.api.model.Stops;
import ch.unstable.ost.utils.ParcelUtils;

class Section implements ch.unstable.ost.api.model.Section, Parcelable {
    // Example: 4
    private String track;
    // Example: Wädenswil
    @SerializedName("terminal")
    private String terminal;

    @SerializedName("number")
    private String motFullName;
    private String type;


    private String name;
    @SerializedName("sbb_name")
    private String sbbName;
    private int runningtime;
    private String stopid;
    private String line;
    private int duration;
    private Stop[] stops;
    private Date departure;
    private Date arrival;
    private SectionExit exit;


    private Section() {

    }

    protected Section(Parcel in) {
        track = in.readString();
        terminal = in.readString();
        motFullName = in.readString();
        type = in.readString();
        runningtime = in.readInt();
        stopid = in.readString();
        line = in.readString();
        duration = in.readInt();
        stops = in.createTypedArray(Stop.CREATOR);
        departure = ParcelUtils.readDate(in);
        arrival = ParcelUtils.readDate(in);
        exit = ParcelUtils.readNullableParcelable(in, SectionExit.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(track);
        dest.writeString(terminal);
        dest.writeString(motFullName);
        dest.writeString(type);
        dest.writeInt(runningtime);
        dest.writeString(stopid);
        dest.writeString(line);
        dest.writeInt(duration);
        dest.writeTypedArray(stops, flags);
        ParcelUtils.writeNullableParcelable(dest, exit, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    @Override
    public String getMoTFullName() {
        return motFullName;
    }

    @Override
    public String getMoTShortName() {
        return line;
    }

    @Override
    public boolean isJourney() {
        return true;
    }

    @Override
    public Date getDepartureTime() {
        return departure;
    }

    @Override
    public Date getArrivalTime() {
        return arrival;
    }

    @Override
    public boolean isWalk() {
        return false;
    }

    @Override
    public String getEndDestination() {
        return terminal;
    }

    @Override
    public String getArrivalStationName() {
        return exit.getSbbName();
    }

    @Override
    public String getDepartureStationName() {
        return sbbName;
    }

    @Override
    public String getDeparturePlatform() {
        return "?";
    }

    @Override
    public String getArrivalPlatform() {
        return "?";
    }

    @Override
    public Stops[] getStops() {
        return stops;
    }
}
