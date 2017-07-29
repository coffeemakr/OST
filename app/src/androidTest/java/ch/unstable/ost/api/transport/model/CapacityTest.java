package ch.unstable.ost.api.transport.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.junit.Test;

import static org.junit.Assert.*;

public class CapacityTest {


    public static <T extends Parcelable> T writeAndRead(T parcelable, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    @Test
    public void writeToParcel() throws Exception {
        Capacity capacity = new Capacity(10, 20);

        Capacity capacityRead = writeAndRead(capacity, Capacity.CREATOR);

        assertEquals("First class", capacity.getFirstClass(), capacityRead.getFirstClass());
        assertEquals("Second class", capacity.getSecondClass(), capacityRead.getSecondClass());

    }

}