package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.HashSet;

import ch.unstable.ost.api.model.Location.StationType;

import static org.junit.Assert.*;

public class StationTypeTest {


    @Test
    public void testSameBit() {
        HashSet<Integer> bits = new HashSet<>(StationType.values().length);
        for(StationType type: StationType.values()) {
            bits.add(type.bit);
        }
        assertEquals(bits.size(), StationType.values().length);
    }

    @Test
    public void getMask() throws Exception {

        int mask = StationType.getMask(StationType.TRAIN, StationType.TRAM);
        assertTrue((mask & StationType.TRAIN.bit) > 0);
        assertTrue((mask & StationType.TRAM.bit) > 0);
        assertTrue((mask & StationType.BUS.bit) == 0);
    }

    @Test
    public void fromMask() throws Exception {
        StationType[] fromMask = StationType.fromMask(StationType.TRAIN.bit | StationType.BUS.bit);
        assertArrayEquals(new StationType[]{StationType.TRAIN, StationType.BUS}, fromMask);
    }

}