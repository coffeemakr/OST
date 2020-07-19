package ch.unstable.ost.api.model;

import org.junit.Test;

import ch.unstable.ost.api.model.Station.StationType;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StationTest {

    private static final String VALID_ID = "id";
    private static final String VALID_NAME = "name";
    private StationType VALID_TYPE = StationType.TRAIN;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullAsName() {
        new Station(null, VALID_TYPE, VALID_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullAsType() {
        new Station(VALID_NAME, null, VALID_ID);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new Station("", VALID_TYPE, VALID_ID);
    }

    @Test
    public void getName() throws Exception {
        Station station = new Station("name", VALID_TYPE, VALID_ID);
        assertEquals("name", station.getName());

        station = new Station("other name", VALID_TYPE, VALID_ID);
        assertEquals("other name", station.getName());
    }

    @Test
    public void getId() throws Exception {
        Station station;
        station = new Station("name", VALID_TYPE, null);
        assertEquals("name", station.getId());

        station = new Station("name", VALID_TYPE, "id");
        assertEquals("id", station.getId());
    }

    @Test
    public void getType() throws Exception {
        Station station;
        station = new Station(VALID_NAME, StationType.TRAIN, VALID_ID);
        assertEquals(StationType.TRAIN, station.getType());
    }

    @Test
    public void equals() throws Exception {
        Station station;
        station = new Station("name", VALID_TYPE, null);
        //noinspection EqualsWithItself
        assertTrue(station.equals(station));
        //noinspection ObjectEqualsNull
        assertFalse(station.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(station.equals("other class"));

        Station other = new Station("name", VALID_TYPE, null);
        assertTrue(other.equals(station));
        assertTrue(station.equals(other));

        other = new Station("name", VALID_TYPE, "name");
        assertFalse(other.equals(station));
        assertFalse(station.equals(other));

        station = new Station("name", StationType.TRAM, "id");
        other = new Station("name", StationType.TRAM, "id");
        assertTrue(other.equals(station));
        assertTrue(station.equals(other));


        other = new Station("name", StationType.BUS, "id");
        assertFalse(other.equals(station));
        assertFalse(station.equals(other));

    }

}