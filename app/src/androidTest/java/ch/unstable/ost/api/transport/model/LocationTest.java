package ch.unstable.ost.api.transport.model;

import org.junit.Test;

import static ch.unstable.ost.api.transport.model.CapacityTest.writeAndRead;
import static org.junit.Assert.assertEquals;


public class LocationTest {
    @Test
    public void writeToParcel() throws Exception {
        String id = "nuuds";
        Location.Type type = Location.Type.POI;
        String name = "name";
        Coordinates coordinates = new Coordinates(10, 20);
        Location location = new Location(id, type, name, coordinates);

        Location readLocation = writeAndRead(location, Location.CREATOR);
        assertEquals(location, readLocation);

        location = new Location(id, null, name, coordinates);
        readLocation = writeAndRead(location, Location.CREATOR);
        assertEquals(location, readLocation);

        location = new Location(id, null, null, coordinates);
        readLocation = writeAndRead(location, Location.CREATOR);
        assertEquals(location, readLocation);

        location = new Location(id, null, null, null);
        readLocation = writeAndRead(location, Location.CREATOR);
        assertEquals(location, readLocation);

    }

}