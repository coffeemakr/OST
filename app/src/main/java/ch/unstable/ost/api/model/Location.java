package ch.unstable.ost.api.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

public interface Location extends Parcelable {

    @NonNull
    String getName();

    @NonNull
    String getId();

    @NonNull
    StationType getType();

    enum StationType {
        TRAIN(1), BUS(2), POI(4), ADDRESS(8), UNKNOWN(16);

        public final int bit;

        StationType(int i) {
            this.bit = i;
        }

        public static int getMask(StationType... types) {
            int mask = 0;
            for (StationType type : types) {
                mask |= type.bit;
            }
            return mask;
        }
    }
}
