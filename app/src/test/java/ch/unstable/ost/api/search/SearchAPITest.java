package ch.unstable.ost.api.search;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ch.unstable.ost.api.model.Connection;

import static ch.unstable.ost.api.transport.TransportAPITest.ONE_HOURS_IN_MILLIES;
import static ch.unstable.ost.api.transport.TransportAPITest.assertConnectionsSortedByDeparture;
import static ch.unstable.ost.api.transport.TransportAPITest.getLast;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SearchAPITest {

    private SearchAPI searchApi;

    @Before
    public void setUp() {
        searchApi = new SearchAPI();
    }

    private static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich"));
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
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

        Connection[] connections = searchApi.getConnections(query, 0);
        assertEquals(connections.length, 4);
        assertConnectionsSortedByDeparture(connections);

        // The last connection should be after the desired arrival date
        Connection lastConnection = getLast(connections);
        assertTrue(lastConnection.getArrivalDate().getTime() > date.getTime());
        assertTrue(lastConnection.getArrivalDate().getTime() < (date.getTime() + ONE_HOURS_IN_MILLIES));
    }


}
