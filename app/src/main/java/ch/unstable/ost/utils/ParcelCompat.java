package ch.unstable.ost.utils;

import android.os.Build;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;


public class ParcelCompat {

    @Nullable
    private static <T extends Parcelable> T readTypeObjectCompat(Parcel in, Parcelable.Creator<T> creator) {
        int exists = in.readInt();
        if (exists == 1) {
            return creator.createFromParcel(in);
        } else if(exists == 0) {
            return null;
        } else {
            throw new ParcelFormatException("Expected exists to be 0 or 1!");
        }
    }

    @Nullable
    public static <T extends Parcelable> T readTypeObject(Parcel in, Parcelable.Creator<T> creator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return in.readTypedObject(creator);
        } else {
            return readTypeObjectCompat(in, creator);
        }
    }


    private static <T extends Parcelable> void writeTypeObjectCompat(Parcel dest, @Nullable T val, int flags) {
        if (val != null) {
            dest.writeInt(1);
            val.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
    }

    public static <T extends Parcelable> void writeTypeObject(Parcel dest, @Nullable T parcelable, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dest.writeTypedObject(parcelable, flags);
        } else {
            writeTypeObjectCompat(dest, parcelable, flags);
        }
    }
}
