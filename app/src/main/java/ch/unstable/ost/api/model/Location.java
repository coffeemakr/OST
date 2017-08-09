package ch.unstable.ost.api.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

public interface Location extends Parcelable {

    /**
     * Get the name of the location
     * @return the name
     */
    @NonNull
    String getName();

    /**
     * Get the unique identifier of the location
     * @return the identifier
     */
    @NonNull
    String getId();

    @NonNull
    StationType getType();

    enum StationType {
        TRAIN(1),
        BUS(2),
        TRAM(4),
        SHIP(8),
        METRO(16),
        CABLEWAY(32),
        COG_RAILWAY(64),
        /// German: Standseilbahn
        FUNICULAR(128),
        ELEVATOR(256),
        POI(512),
        ADDRESS(1024),
        UNKNOWN(2048);

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

        public static StationType[] fromMask(int mask) {
            StationType[] types = new StationType[values().length];
            int length = 0;
            for(StationType type: values()) {
                if((mask & type.bit) > 0) {
                    types[length] = type;
                    ++length;
                }
            }
            return Arrays.copyOf(types, length);
        }
    }
}
