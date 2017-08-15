package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.Date;

import ch.unstable.ost.test.TestHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;


public class SectionTest {

    private static final Route VALID_ROUTE = TestHelper.generateRandomRoute(321);
    private static final DepartureCheckpoint VALID_DEPARTURE = TestHelper.generateDepartureCheckpoint(12);
    private static final ArrivalCheckpoint VALID_ARRIVAL = TestHelper.generateArrivalCheckpoint(94);
    private static final String VALID_HEADSIGN = "Head";
    private static final long VALID_WALKTIME = 1000;


    @Test
    public void describeContents() throws Exception {
        Section section = new Section(VALID_ROUTE, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(0, section.describeContents());
    }

    @Test
    public void getLineShortName() throws Exception {
        String shortName = "Short Name";
        String longName = "Long Name";
        PassingCheckpoint[] stops = TestHelper.generatePassingCheckpoints(213);
        Route route = new Route(shortName, longName, stops);
        Section section = new Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(shortName, section.getLineShortName());
    }

    @Test
    public void getRouteLongName() throws Exception {
        String shortName = "Short Name";
        String longName = "Long Name";
        PassingCheckpoint[] stops = new PassingCheckpoint[0];
        Route route = new Route(shortName, longName, stops);
        Section section = new Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(longName, section.getRouteLongName());
    }

    @Test
    public void getRouteStops() throws Exception {
        String shortName = "Short Name";
        String longName = "Long Name";
        PassingCheckpoint[] stops = TestHelper.generatePassingCheckpoints(100);
        Route route = new Route(shortName, longName, stops);
        Section section = new Section(route, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertNotSame(stops, section.getStops());
        assertArrayEquals(stops, section.getStops());
    }




    @Test
    public void getDeparture() throws Exception {
        Date departureTime = new Date();
        Location location = mock(Location.class);
        DepartureCheckpoint departureCheckpoint = new DepartureCheckpoint(departureTime, "21", location);
        Section section = new Section(VALID_ROUTE, departureCheckpoint, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(departureTime, section.getDepartureDate());
        assertEquals(location, section.getDepartureLocation());
        assertEquals("21", section.getDeparturePlatform());
        assertSame(departureCheckpoint, section.getDeparture());

    }

    @Test
    public void getArrival() throws Exception {
        Date arrivalTime = new Date();
        Location location = mock(Location.class);
        ArrivalCheckpoint arrivalCheckpoint = new ArrivalCheckpoint(arrivalTime, "21", location);
        Section section = new Section(VALID_ROUTE, VALID_DEPARTURE, arrivalCheckpoint, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(arrivalTime, section.getArrivalDate());
        assertEquals(location, section.getArrivalLocation());
        assertEquals("21", section.getArrivalPlatform());
        assertSame(arrivalCheckpoint, section.getArrival());
    }

    @Test
    public void getHeadsign() throws Exception {
        Section section = new Section(VALID_ROUTE, VALID_DEPARTURE, VALID_ARRIVAL, VALID_HEADSIGN, VALID_WALKTIME);
        assertEquals(VALID_HEADSIGN, section.getHeadsign());
    }
}