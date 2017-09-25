package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects;

import java.util.Arrays;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final Section[] sections;

    /**
     * Create a new connection
     * @param sections an array containing the sections
     */
    public Connection(Section[] sections) {
        this.sections = checkNotNull(sections, "sections");
    }

    /**
     * Create a new connection from a parcel
     * @param in the parcel
     */
    private Connection(Parcel in) {
        sections = in.createTypedArray(Section.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(sections, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Get the sections
     * @return the sections
     */
    @NonNull
    public Section[] getSections() {
        return Arrays.copyOf(sections, sections.length);
    }

    /**
     * Get the departure time
     * @return the time of the departure
     */
    public Date getDepartureDate() {
        return sections[0].getDepartureDate();
    }

    /**
     * Get the arrival time
     * @return the arrival time
     */
    public Date getArrivalDate() {
        return sections[sections.length - 1].getArrivalDate();
    }


    /**
     * Get the departure checkpoint
     *
     * The departure checkpoint of a connection is the departure checkpoint of the first section.
     * @return the departure checkpoint
     */
    public DepartureCheckpoint getDeparture() {
        return sections[0].getDeparture();
    }

    /**
     * Get the arrival checkpoint
     *
     * The arrival checkpoint of a connection is the arrival checkpoint of the last section.
     * @return the arrival checkpoint
     */
    public ArrivalCheckpoint getArrival() {
        return sections[sections.length - 1].getArrival();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sections", sections)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Arrays.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sections);
    }

    public String getDepartureName() {
        return getDeparture().getLocation().getName();
    }

    public String getArrivalName() {
        return getArrival().getLocation().getName();
    }
}
