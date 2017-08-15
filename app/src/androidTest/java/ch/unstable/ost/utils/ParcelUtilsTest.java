package ch.unstable.ost.utils;

import android.os.Parcel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;


public class ParcelUtilsTest {
    private Parcel parcel;

    @Before
    public void setUp() {
        parcel = Parcel.obtain();
    }

    @After
    public void tearDown() {
        parcel.recycle();
    }

    @Test
    public void readDate() throws Exception {
        parcel.writeLong(-1);
        parcel.setDataPosition(0);
        assertNull(ParcelUtils.readDate(parcel));

        parcel.setDataPosition(0);

        Date date  = new Date();
        assertNotEquals(-1, date.getTime());
        parcel.writeLong(date.getTime());
        parcel.setDataPosition(0);
        assertEquals(date, ParcelUtils.readDate(parcel));
    }

    @Test
    public void writeDate() throws Exception {
        Date date = new Date();
        ParcelUtils.writeDate(parcel, date);
        parcel.setDataPosition(0);
        assertEquals(date.getTime(), parcel.readLong());

        parcel.setDataPosition(0);
        assertEquals(date, ParcelUtils.readDate(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeDate(parcel, null);

        parcel.setDataPosition(0);
        assertEquals(-1, parcel.readLong());
    }

    @Test
    public void writeNullableInteger() throws Exception {
        ParcelUtils.writeNullableInteger(parcel, null);
        parcel.setDataPosition(0);
        assertNull(ParcelUtils.readNullableInteger(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableInteger(parcel, 10);
        parcel.setDataPosition(0);
        assertEquals(Integer.valueOf(10), ParcelUtils.readNullableInteger(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableInteger(parcel, 0);
        parcel.setDataPosition(0);
        assertEquals(Integer.valueOf(0), ParcelUtils.readNullableInteger(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableInteger(parcel, -1);
        parcel.setDataPosition(0);
        assertEquals(Integer.valueOf(-1), ParcelUtils.readNullableInteger(parcel));
    }

    @Test
    public void writeNullableLong() throws Exception {
        ParcelUtils.writeNullableLong(parcel, null);
        parcel.setDataPosition(0);
        assertNull(ParcelUtils.readNullableLong(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableLong(parcel, 10L);
        parcel.setDataPosition(0);
        assertEquals(Long.valueOf(10), ParcelUtils.readNullableLong(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableLong(parcel, 0L);
        parcel.setDataPosition(0);
        assertEquals(Long.valueOf(0), ParcelUtils.readNullableLong(parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeNullableLong(parcel, -1L);
        parcel.setDataPosition(0);
        assertEquals(Long.valueOf(-1), ParcelUtils.readNullableLong(parcel));
    }
}