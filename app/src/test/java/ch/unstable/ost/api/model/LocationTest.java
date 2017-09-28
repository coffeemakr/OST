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

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsName() {
        new Location(VALID_ID, null, VALID_TYPE);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsType() {
        new Location(VALID_ID, VALID_NAME, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        new Location(VALID_ID, "", VALID_TYPE);
    }

    @Test
    public void getName() throws Exception {
        Location location = new Location(VALID_ID, "name", VALID_TYPE);
        assertEquals("name", location.getName());

        location = new Location(VALID_ID, "other name", VALID_TYPE);
        assertEquals("other name", location.getName());
    }

    @Test
    public void getId() throws Exception {
        Location location;
        location = new Location(null, "name", VALID_TYPE);
        assertEquals("name", location.getId());

        location = new Location("id", "name", VALID_TYPE);
        assertEquals("id", location.getId());
    }

    @Test
    public void getType() throws Exception {
        Location location;
        location = new Location(VALID_ID, VALID_NAME, StationType.TRAIN);
        assertEquals(StationType.TRAIN, location.getType());
    }

    @Test
    public void equals() throws Exception {
        Location location;
        location = new Location(null, "name", VALID_TYPE);
        //noinspection EqualsWithItself
        assertTrue(location.equals(location));
        //noinspection ObjectEqualsNull
        assertFalse(location.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(location.equals("other class"));

        Location other = new Location(null, "name", VALID_TYPE);
        assertTrue(other.equals(location));
        assertTrue(location.equals(other));

        other = new Location("name", "name", VALID_TYPE);
        assertFalse(other.equals(location));
        assertFalse(location.equals(other));

        location = new Location("id", "name", StationType.TRAM);
        other = new Location("id", "name", StationType.TRAM);
        assertTrue(other.equals(location));
        assertTrue(location.equals(other));


        other = new Location("id", "name", StationType.BUS);
        assertFalse(other.equals(location));
        assertFalse(location.equals(other));

    }

}