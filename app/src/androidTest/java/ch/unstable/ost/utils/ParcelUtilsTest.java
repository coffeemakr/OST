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

    }

    @Test
    public void readNullableInteger() throws Exception {

    }

    @Test
    public void readEnum() throws Exception {

    }

    @Test
    public void writeEnum() throws Exception {

    }

    @Test
    public void readNullableParcelable() throws Exception {

    }

    @Test
    public void writeNullableParcelable() throws Exception {

    }

    @Test
    public void writeNullableLong() throws Exception {

    }

    @Test
    public void readNullableLong() throws Exception {

    }

}