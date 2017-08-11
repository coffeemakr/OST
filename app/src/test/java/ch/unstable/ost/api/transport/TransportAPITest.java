package ch.unstable.ost.api.transport;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Section;

import static org.junit.Assert.*;


public class TransportAPITest {
    public static final int ONE_HOURS_IN_MILLIES = 60 * 60 * 1000;
    private TransportAPI transportApi;

    @Before
    public void setUp() {
        transportApi = new TransportAPI();
    }

    private static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich"));
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
    }

    private <E> E getFirst(E[] entities) {
        return entities[0];
    }

    private <E> E getLast(E[] entities) {
        return entities[entities.length - 1];
    }

    private static void assertConnectionsSortedByDeparture(Connection[] connections) {
        assertIsSorted(connections, new Comparator<Connection>() {
            @Override
            public int compare(Connection o1, Connection o2) {
                return o1.getDepartureDate().compareTo(o2.getDepartureDate());
            }
        });
    }

    private static <E> void assertIsSorted(E[] entries, Comparator<E> comparator) {
        E[] copy = Arrays.copyOf(entries, entries.length);
        Arrays.sort(copy, comparator);
        assertArrayEquals("Array changed beeing sorted", entries, copy);
    }

    @Test
    public void getConnections() throws Exception {
        Date date = getDate(2017,7,7,10,0);
        System.out.println(date);
        ConnectionQuery query = new ConnectionQuery.Builder()
                .setFrom("Bern")
                .setTo("Basel SBB")
                .addVia("Genf")
                .setArrivalTime(date)
                .build();

        Connection[] connections = transportApi.getConnections(query, 0);
        assertEquals(connections.length, 6);
        assertConnectionsSortedByDeparture(connections);
        for(Connection connection: connections) {
            System.out.println(connection.getArrivalDate());
        }

        // The last connection should be after the desired arrival date
        Connection lastConnection = getLast(connections);
        assertTrue(lastConnection.getArrivalDate().getTime() > date.getTime());
        assertTrue(lastConnection.getArrivalDate().getTime() < (date.getTime() + ONE_HOURS_IN_MILLIES));
   }

   @Test
    public void getConnectionsWithDepartureTimeSet() throws Exception {

       Date date = getDate(2017,7,7,10,0);

       ConnectionQuery query = new ConnectionQuery.Builder()
               .setFrom("Bern")
               .setTo("Basel SBB")
               .addVia("Genf")
               .setDepartureTime(date)
               .build();

       Connection[] connections = transportApi.getConnections(query, 0);
       assertEquals(connections.length, 6);
       assertConnectionsSortedByDeparture(connections);

       for(Connection connection: connections) {
           System.out.println(connection.getDeparture());
       }
       // First can be before the selected departure that's fine for me
       Connection firstConnection = getFirst(connections);
       assertTrue(firstConnection.getDepartureDate().getTime() < date.getTime());
       assertTrue(firstConnection.getDepartureDate().getTime() > (date.getTime() - ONE_HOURS_IN_MILLIES));

       for(Connection connection: connections) {
           assertEquals("Bern", connection.getDeparture().getLocation().getName());
           assertEquals("8507000", connection.getDeparture().getLocation().getId());

           Section firstSection = getFirst(connection.getSections());
           assertSame(connection.getDeparture(), firstSection.getDeparture());

           Section lastSection = getLast(connection.getSections());
           assertSame(connection.getArrival(), lastSection.getArrival());

           assertEquals("Basel SBB", connection.getArrival().getLocation().getName());
           assertEquals("8500010", connection.getArrival().getLocation().getId());
       }
   }
}