package ch.unstable.ost.api.model;

import android.os.Parcelable;

public interface Station extends Parcelable{
    String getName();

    String getId();

    StationType getType();

    enum StationType {
        TRAIN(1 << 0), BUS(1 << 1), POI(1 << 2), ADDRESS(1 << 3), UNKNOWN(1 << 4);

        public final int bit;

        StationType(int i) {
            this.bit = i;
        }

        public static int getMask(StationType... types) {
            int mask = 0;
            for(StationType type: types) {
                mask |= type.bit;
            }
            return mask;
        }
    }
}
