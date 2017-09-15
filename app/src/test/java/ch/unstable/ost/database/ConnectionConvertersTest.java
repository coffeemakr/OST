package ch.unstable.ost.database;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 15.09.17.
 */
public class ConnectionConvertersTest {
    @Test
    public void stringArrayToCSV() throws Exception {
        String value = ConnectionConverters.stringArrayToCSV(new String[]{"hallo", "welt"});
        System.out.println(value);

        assertArrayEquals(new String[]{"hallo", "welt"}, ConnectionConverters.csvToStringArray(value));
    }

}