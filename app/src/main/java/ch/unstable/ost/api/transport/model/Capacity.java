package ch.unstable.ost.api.transport.model;

import android.os.Parcel;
import android.os.Parcelable;

class Capacity implements Parcelable {

    public static final Creator<Capacity> CREATOR = new Creator<Capacity>() {
        @Override
        public Capacity createFromParcel(Parcel in) {
            return new Capacity(in);
        }

        @Override
        public Capacity[] newArray(int size) {
            return new Capacity[size];
        }
    };
    private final int firstClass;
    private final int secondClass;

    public Capacity(int firstClass, int secondClass) {
        this.firstClass = firstClass;
        this.secondClass = secondClass;
    }

    protected Capacity(Parcel in) {
        firstClass = in.readInt();
        secondClass = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(firstClass);
        dest.writeInt(secondClass);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getFirstClass() {
        return firstClass;
    }

    public int getSecondClass() {
        return secondClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Capacity capacity = (Capacity) o;

        if (firstClass != capacity.firstClass) return false;
        return secondClass == capacity.secondClass;

    }

    @Override
    public int hashCode() {
        int result = firstClass;
        result = 31 * result + secondClass;
        return result;
    }
}
