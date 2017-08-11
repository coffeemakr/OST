package ch.unstable.ost.utils;


import android.support.annotation.NonNull;

public class ObjectsCompat {
    private ObjectsCompat() {
    }

    @NonNull
    public static <E> E requireNonNull(E object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " is null");
        }
        return object;
    }

    public static <E extends CharSequence> E requireNonEmpty(E value, String name) {
        requireNonNull(value, name);
        if(value.length() == 0) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return value;
    }
}
