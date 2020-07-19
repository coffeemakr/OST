package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.HashSet;

import ch.unstable.ost.api.model.Station.StationType;

import static org.junit.Assert.*;

public class StationTypeTest {


    @Test
    public void testSameBit() {
        HashSet<Integer> bits = new HashSet<>(StationType.values().length);
        for(StationType type: StationType.values()) {
            bits.add(type.getBit());
        }
        assertEquals(bits.size(), StationType.values().length);
    }

    @Test
    public void getMask() throws Exception {

        int mask = StationType.Companion.getMask(StationType.TRAIN, StationType.TRAM);
        assertTrue((mask & StationType.TRAIN.getBit()) > 0);
        assertTrue((mask & StationType.TRAM.getBit()) > 0);
        assertTrue((mask & StationType.BUS.getBit()) == 0);
    }

    @Test
    public void fromMask() throws Exception {
        StationType[] fromMask = StationType.Companion.fromMask(StationType.TRAIN.getBit() | StationType.BUS.getBit());
        assertArrayEquals(new StationType[]{StationType.TRAIN, StationType.BUS}, fromMask);
    }

}