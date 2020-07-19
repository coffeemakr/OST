package ch.unstable.ost.utils;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import com.google.common.base.Objects;

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

    private enum TestEnum {
        ONE, TWO, THREE
    }

    private static class TestParcelable implements Parcelable {

        private final int integer;
        private final String string;

        public TestParcelable(int integer, String string) {
            this.integer = integer;
            this.string = string;
        }

        private TestParcelable(Parcel in) {
            integer = in.readInt();
            string = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(integer);
            dest.writeString(string);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
            @Override
            public TestParcelable createFromParcel(Parcel in) {
                return new TestParcelable(in);
            }

            @Override
            public TestParcelable[] newArray(int size) {
                return new TestParcelable[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestParcelable that = (TestParcelable) o;
            return integer == that.integer &&
                    Objects.equal(string, that.string);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(integer, string);
        }
    }

    @Test
    public void writeNonNullTypedObject() throws Exception {
        TestParcelable testParcelable = new TestParcelable(123, "string");
        parcel.setDataPosition(0);
        ParcelUtils.writeNonNullTypedObject(parcel, testParcelable, 0);
        parcel.setDataPosition(0);
        assertEquals(testParcelable, ParcelUtils.readNonNulTypedObject(parcel, TestParcelable.CREATOR));
    }

    @Test(expected = ParcelFormatException.class)
    public void readNonNullTypedObjectWithNull() throws Exception {
        parcel.setDataPosition(0);
        ParcelCompat.writeTypeObject(parcel, null, 0);
        parcel.setDataPosition(0);
        ParcelUtils.readNonNulTypedObject(parcel, TestParcelable.CREATOR);
    }

    @Test
    public void writeEnum() throws Exception {
        parcel.setDataPosition(0);
        ParcelUtils.writeEnum(parcel, TestEnum.THREE);
        parcel.setDataPosition(0);
        assertEquals(TestEnum.THREE, ParcelUtils.readEnum(TestEnum.values(), parcel));

        parcel.setDataPosition(0);
        ParcelUtils.writeEnum(parcel, null);
        parcel.setDataPosition(0);
        assertEquals(null, ParcelUtils.readEnum(TestEnum.values(), parcel));
    }
}