package ch.unstable.ost;

import android.os.Parcel;
import android.os.Parcelable;

public class TestHelper {
    public static <T extends Parcelable> T writeAndRead(T parcelable, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
