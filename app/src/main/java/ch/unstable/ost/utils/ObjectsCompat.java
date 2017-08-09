package ch.unstable.ost.utils;


import android.support.annotation.NonNull;

public class ObjectsCompat {
    private ObjectsCompat(){}

    @NonNull
    public static <E> E requireNonNull(E object, String name) {
        if(object == null) {
            throw new NullPointerException(name + " is null");
        }
        return object;
    }
}
