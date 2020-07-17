package ch.unstable.ost.utils;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

public enum ParcelUtils {
    ;

    @Nullable
    public static Date readDate(Parcel in) {
        long timestamp = in.readLong();
        if (timestamp == -1) {
            return null;
        } else {
            return new Date(timestamp);
        }
    }

    public static void writeDate(Parcel out, @Nullable Date date) {
        if (date == null) {
            out.writeLong(-1);
        } else {
            out.writeLong(date.getTime());
        }
    }

    public static void writeNullableInteger(Parcel out, @Nullable Integer integer) {
        if (integer == null) {
            out.writeInt(Integer.MIN_VALUE);
        } else {
            out.writeInt(integer);
        }
    }

    @Nullable
    public static Integer readNullableInteger(Parcel in) {
        int value = in.readInt();
        if (value == Integer.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }

    public static <E> E readEnum(E[] values, Parcel in) {
        int ordinal = in.readInt();
        if (ordinal == -1) {
            return null;
        } else {
            return values[ordinal];
        }
    }

    public static void writeEnum(Parcel dest, @Nullable Enum enumeration) {
        if (enumeration == null) {
            dest.writeInt(-1);
        } else {
            dest.writeInt(enumeration.ordinal());
        }
    }


    @Nullable
    public static <T extends Parcelable> T readParcelable(Parcel in, Parcelable.Creator<T> creator) {
        return ParcelCompat.readTypeObject(in, creator);
    }

    public static void writeParcelable(Parcel dest, Parcelable parcelable, int flags) {
        ParcelCompat.writeTypeObject(dest, parcelable, flags);
    }

    public static void writeNullableLong(Parcel dest, @Nullable Long value) {
        if (value != null) {
            dest.writeLong(value);
        } else {
            dest.writeLong(Long.MIN_VALUE);
        }
    }

    @Nullable
    public static Long readNullableLong(Parcel in) {
        long value = in.readLong();
        if (value == Long.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }

    @NonNull
    public static <T extends Parcelable> T readNonNulTypedObject(Parcel in, Parcelable.Creator<T> creator) {
        T value = ParcelCompat.readTypeObject(in, creator);
        if (value == null) {
            throw new ParcelFormatException("Read value can't be null");
        }
        return value;
    }

    public static <T extends Parcelable> void writeNonNullTypedObject(Parcel dest, T value, int flags) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(value, "value is null");
        ParcelCompat.writeTypeObject(dest, value, flags);
    }
}
