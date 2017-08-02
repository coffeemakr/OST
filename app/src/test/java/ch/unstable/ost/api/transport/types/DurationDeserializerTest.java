package ch.unstable.ost.api.transport.types;

import org.junit.Test;

import static org.junit.Assert.*;


public class DurationDeserializerTest {

    private static final int MINUTES = 60;
    private static final int HOURS = 60 * MINUTES;
    private static final long DAYS = 24 * HOURS;
    @Test
    public void fromString() throws Exception {
        Long result = DurationDeserializer.fromString("00:00:10");
        assertNotNull(result);
        assertEquals(10L, (long) result);

        result = DurationDeserializer.fromString("10d00:00:10");
        assertNotNull(result);
        assertEquals(10 * DAYS + 10L, (long) result);

        result = DurationDeserializer.fromString("10d20:00:10");
        assertNotNull(result);
        assertEquals(10 * DAYS + 20 * HOURS + 10L, (long) result);
    }

}