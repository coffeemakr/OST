package ch.unstable.ost.api.model;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;


public class ConnectionQueryBuilderTest {

    private static final String VALID_TO = "Basel SBB";
    private static final String VALID_FROM = "Zürich HB";

    @Test(expected = IllegalStateException.class)
    public void testBuildWithoutFrom() {
        new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildWithoutTo() {
        new ConnectionQuery.Builder()
                .setFrom(VALID_FROM)
                .build();
    }

    @Test
    public void setTo() {
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder();
        builder.setTo("To");
        builder.setFrom(VALID_FROM);
        ConnectionQuery query = builder.build();
        assertEquals("To", query.getTo());
    }

    @Test(expected = NullPointerException.class)
    public void setToWithNull() {
        new ConnectionQuery.Builder().setTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setToWithEmptyString() {
        new ConnectionQuery.Builder().setTo("");
    }


    @Test(expected = NullPointerException.class)
    public void setFromWithNull() {
        new ConnectionQuery.Builder().setFrom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFromWithEmptyString() {
        new ConnectionQuery.Builder().setFrom("");
    }

    @Test
    public void setFrom() {
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder();
        builder.setTo(VALID_TO);
        builder.setFrom("From");
        ConnectionQuery query = builder.build();
        assertEquals("From", query.getFrom());
    }

    @Test
    public void addVia() {
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .setFrom(VALID_FROM);

        builder.addVia("Genève");

        ConnectionQuery query = builder.build();
        assertArrayEquals(new String[]{"Genève"}, query.getVia());

        builder.addVia("Basel, Zoo");
        query = builder.build();
        assertArrayEquals(new String[]{"Genève", "Basel, Zoo"}, query.getVia());
    }


    @Test
    public void setVia() {
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .setFrom(VALID_FROM);

        builder.addVia("Ostermundigen");
        builder.setVia("Bern", "Wynau");
        builder.addVia("Roggwil");
        ConnectionQuery query = builder.build();
        assertArrayEquals(new String[]{"Bern", "Wynau", "Roggwil"}, query.getVia());
        assertTrue(query.hasVia());

        builder.setVia((String[]) null);
        query = builder.build();
        assertTrue(query.getVia().length == 0);
        assertFalse(query.hasVia());

        builder.setVia();
        query = builder.build();
        assertTrue(query.getVia().length == 0);
        assertFalse(query.hasVia());

    }

    @Test
    public void setArrivalDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 12, 11, 10, 9, 8);
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .setFrom(VALID_FROM);

        builder.setDepartureTime(new Date());
        builder.setArrivalTime(calendar.getTime());

        ConnectionQuery query = builder.build();
        assertEquals(calendar.getTime(), query.getArrivalTime());
        assertNull(query.getDepartureTime());
    }

    @Test
    public void setDepartureDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 12, 30, 12, 10, 8);
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .setFrom(VALID_FROM);

        builder.setArrivalTime(new Date());
        builder.setDepartureTime(calendar.getTime());

        ConnectionQuery query = builder.build();
        assertEquals(calendar.getTime(), query.getDepartureTime());
        assertNull(query.getArrivalTime());
    }

    @Test
    public void reverseDirection() {
        ConnectionQuery.Builder builder = new ConnectionQuery.Builder()
                .setTo(VALID_TO)
                .setFrom(VALID_FROM);

        builder.reverseDirection();

        ConnectionQuery query = builder.build();
        assertEquals(VALID_FROM, query.getTo());
        assertEquals(VALID_TO, query.getFrom());
    }
}