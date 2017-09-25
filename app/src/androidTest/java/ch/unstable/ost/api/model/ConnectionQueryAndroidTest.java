package ch.unstable.ost.api.model;


import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import ch.unstable.ost.test.TestHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ConnectionQueryAndroidTest {
    @Test
    public void writeParcel() {
        String to = "Zürich, HB";
        String from = "Basel SBB";
        ConnectionQuery query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();

        ConnectionQuery read = TestHelper.writeAndRead(query, ConnectionQuery.CREATOR);
        assertEquals(to, read.getTo());
        assertEquals(from, read.getFrom());
        assertNull(read.getArrivalTime());
        assertNull(read.getDepartureTime());
        assertTrue(read.getVia().length == 0);
        assertEquals(read, query);


        String[] vias = new String[]{"Baden, Historisches Museum"};
        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setVia(vias)
                .build();

        read = TestHelper.writeAndRead(query, ConnectionQuery.CREATOR);

        assertEquals(to, read.getTo());
        assertEquals(from, read.getFrom());
        assertNull(read.getArrivalTime());
        assertNull(read.getDepartureTime());
        assertArrayEquals(vias, read.getVia());
        assertEquals(read, query);

        Date date = new Date();
        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setDepartureTime(date)
                .build();

        read = TestHelper.writeAndRead(query, ConnectionQuery.CREATOR);
        assertEquals(to, read.getTo());
        assertEquals(from, read.getFrom());
        assertNull(read.getArrivalTime());
        assertEquals(date, read.getDepartureTime());
        assertTrue(read.getVia().length == 0);
        assertEquals(read, query);

        date = new Date();
        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setArrivalTime(date)
                .build();

        read = TestHelper.writeAndRead(query, ConnectionQuery.CREATOR);
        assertEquals(to, read.getTo());
        assertEquals(from, read.getFrom());
        assertNull(read.getDepartureTime());
        assertEquals(date, read.getArrivalTime());
        assertTrue(read.getVia().length == 0);
        assertFalse(read.hasVia());
        assertEquals(read, query);

    }
}
