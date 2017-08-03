package ch.unstable.ost.api.transport.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class Coordinates implements Parcelable {
    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
    public final double x;
    public final double y;

    protected Coordinates(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}
