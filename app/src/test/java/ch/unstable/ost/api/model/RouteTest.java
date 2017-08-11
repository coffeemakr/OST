package ch.unstable.ost.api.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RouteTest {

    private String validShortName;
    private String validLongName;
    private PassingCheckpoint[] validStops;

    @Before
    public void setUp() {
        validShortName = "shortname";
        validLongName = "longname";
        validStops = new PassingCheckpoint[0];
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsShortName() {
        new Route(null, validLongName, validStops);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsLongName() {
        new Route(validShortName, null, validStops);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsStops() {
        new Route(validShortName, validLongName, null);
    }

    @Test
    public void getShortName() throws Exception {
        Route route = new Route("shortname", validLongName, validStops);
        assertEquals("shortname", route.getShortName());
    }

    @Test
    public void getLongName() throws Exception {
        Route route = new Route(validShortName, "long name", validStops);
        assertEquals("long name", route.getLongName());
    }

    @Test
    public void getStops() throws Exception {
        PassingCheckpoint stops[] = new PassingCheckpoint[1];
        stops[0] = mock(PassingCheckpoint.class);
        Route route = new Route(validShortName, validLongName, stops);
        assertArrayEquals(stops, route.getStops());
    }

}