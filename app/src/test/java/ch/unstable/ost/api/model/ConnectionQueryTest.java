package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ConnectionQueryTest {
    @Test
    public void testEquals() throws Exception {
        String to = "Zürich, HB";
        String from = "Basel SBB";


        ConnectionQuery query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();
        ConnectionQuery other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();

        assertTrue(query.equals(other));
        assertTrue(other.equals(query));
        assertEquals(query.hashCode(), other.hashCode());

        //noinspection ObjectEqualsNull
        assertFalse(query.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(query.equals("other class"));
        //noinspection EqualsWithItself
        assertTrue(query.equals(query));


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();
        other = new ConnectionQuery.Builder()
                .setTo(from)
                .setFrom(to)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();
        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(to)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());

        other = new ConnectionQuery.Builder()
                .addVia("Genf")
                .setTo(to)
                .setFrom(from)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .addVia("Genf")
                .build();

        assertTrue(query.equals(other));
        assertTrue(other.equals(query));
        assertEquals(query.hashCode(), other.hashCode());


        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .addVia("Genf")
                .addVia("Lausanne")
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .addVia("Genf")
                .addVia("Lausanne")
                .build();

        assertTrue(query.equals(other));
        assertTrue(other.equals(query));
        assertEquals(query.hashCode(), other.hashCode());


        Date date = new Date();
        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setArrivalTime(date)
                .build();

        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setArrivalTime(date)
                .build();

        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setDepartureTime(date)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());



        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setDepartureTime(date)
                .build();

        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .build();

        assertFalse(query.equals(other));
        assertFalse(other.equals(query));
        assertNotEquals(query.hashCode(), other.hashCode());


        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setDepartureTime(date)
                .build();

        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setDepartureTime(date)
                .build();

        assertTrue(query.equals(other));
        assertTrue(other.equals(query));
        assertEquals(query.hashCode(), other.hashCode());

        query = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setArrivalTime(date)
                .build();

        other = new ConnectionQuery.Builder()
                .setTo(to)
                .setFrom(from)
                .setArrivalTime(date)
                .build();

        assertTrue(query.equals(other));
        assertTrue(other.equals(query));
        assertEquals(query.hashCode(), other.hashCode());

    }

    @Test
    public void testDescribeContent() {
        ConnectionQuery query = new ConnectionQuery.Builder()
                .setTo("to")
                .setFrom("from")
                .build();
        assertEquals(0, query.describeContents());
    }

}