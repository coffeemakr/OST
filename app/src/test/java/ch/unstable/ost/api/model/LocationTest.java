package ch.unstable.ost.api.model;

import org.junit.Test;

import ch.unstable.ost.api.model.Location.StationType;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocationTest {

    private static final String VALID_ID = "id";
    private static final String VALID_NAME = "name";
    private StationType VALID_TYPE = StationType.TRAIN;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullAsName() {
        new Location(null, VALID_TYPE, VALID_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullAsType() {
        new Location(VALID_NAME, null, VALID_ID);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new Location("", VALID_TYPE, VALID_ID);
    }

    @Test
    public void getName() throws Exception {
        Location location = new Location("name", VALID_TYPE, VALID_ID);
        assertEquals("name", location.getName());

        location = new Location("other name", VALID_TYPE, VALID_ID);
        assertEquals("other name", location.getName());
    }

    @Test
    public void getId() throws Exception {
        Location location;
        location = new Location("name", VALID_TYPE, null);
        assertEquals("name", location.getId());

        location = new Location("name", VALID_TYPE, "id");
        assertEquals("id", location.getId());
    }

    @Test
    public void getType() throws Exception {
        Location location;
        location = new Location(VALID_NAME, StationType.TRAIN, VALID_ID);
        assertEquals(StationType.TRAIN, location.getType());
    }

    @Test
    public void equals() throws Exception {
        Location location;
        location = new Location("name", VALID_TYPE, null);
        //noinspection EqualsWithItself
        assertTrue(location.equals(location));
        //noinspection ObjectEqualsNull
        assertFalse(location.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(location.equals("other class"));

        Location other = new Location("name", VALID_TYPE, null);
        assertTrue(other.equals(location));
        assertTrue(location.equals(other));

        other = new Location("name", VALID_TYPE, "name");
        assertFalse(other.equals(location));
        assertFalse(location.equals(other));

        location = new Location("name", StationType.TRAM, "id");
        other = new Location("name", StationType.TRAM, "id");
        assertTrue(other.equals(location));
        assertTrue(location.equals(other));


        other = new Location("name", StationType.BUS, "id");
        assertFalse(other.equals(location));
        assertFalse(location.equals(other));

    }

}