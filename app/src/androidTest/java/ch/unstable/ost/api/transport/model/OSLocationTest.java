package ch.unstable.ost.api.transport.model;

import org.junit.Test;

import static ch.unstable.ost.api.transport.model.CapacityTest.writeAndRead;
import static org.junit.Assert.assertEquals;


public class OSLocationTest {
    @Test
    public void writeToParcel() throws Exception {
        String id = "nuuds";
        OSLocation.Type type = OSLocation.Type.POI;
        String name = "name";
        Coordinates coordinates = new Coordinates(10, 20);
        OSLocation location = new OSLocation(id, type, name, coordinates);

        OSLocation readLocation = writeAndRead(location, OSLocation.CREATOR);
        assertEquals(location, readLocation);

        location = new OSLocation(id, null, name, coordinates);
        readLocation = writeAndRead(location, OSLocation.CREATOR);
        assertEquals(location, readLocation);

        location = new OSLocation(id, null, null, coordinates);
        readLocation = writeAndRead(location, OSLocation.CREATOR);
        assertEquals(location, readLocation);

        location = new OSLocation(id, null, null, null);
        readLocation = writeAndRead(location, OSLocation.CREATOR);
        assertEquals(location, readLocation);

    }

}