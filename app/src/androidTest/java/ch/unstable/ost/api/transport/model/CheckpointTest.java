package ch.unstable.ost.api.transport.model;

import org.junit.Test;

import java.util.Date;

import ch.unstable.ost.api.model.Location.StationType;

import static ch.unstable.ost.api.transport.model.CapacityTest.writeAndRead;
import static org.junit.Assert.*;


public class CheckpointTest {
    @Test
    public void writeToParcel() throws Exception {
        String id = "321i312";
        Location.InternalType type = null;
        String name = "name";
        Coordinates coordinates = new Coordinates(10, 10);
        Location location = new Location(id, type, name, coordinates);
        Checkpoint checkpoint = new Checkpoint(location);

        Checkpoint readCheckPoint = writeAndRead(checkpoint, Checkpoint.CREATOR);

        assertEquals(id, readCheckPoint.getStation().getId());
        assertEquals(StationType.UNKNOWN, readCheckPoint.getStation().getType());
        assertEquals(name, readCheckPoint.getStation().getName());
        assertEquals(coordinates, readCheckPoint.getStation().getCoordinates());
        assertNull(readCheckPoint.getArrival());
        assertNull(readCheckPoint.getDepartureTime());
        assertNull(readCheckPoint.getDelay());
        assertNull(readCheckPoint.getPlatform());

        Date arrival = new Date();
        Date departure = new Date(1000);
        Integer delay = 10;
        String platform = "13/21";
        id = "321312";
        type = Location.InternalType.ADDRESS;
        name = "something";
        coordinates = new Coordinates(200, 100);
        location = new Location(id, type, name, coordinates);
        checkpoint = new Checkpoint(location, arrival, departure, delay, platform);

        readCheckPoint = writeAndRead(checkpoint, Checkpoint.CREATOR);
        assertEquals(id, readCheckPoint.getStation().getId());
        assertEquals(StationType.ADDRESS, readCheckPoint.getStation().getType());
        assertEquals(name, readCheckPoint.getStation().getName());
        assertEquals(coordinates, readCheckPoint.getStation().getCoordinates());
        assertEquals("Arrival", arrival, readCheckPoint.getArrival());
        assertEquals("Departure", departure, readCheckPoint.getDepartureTime());
        assertEquals(delay, readCheckPoint.getDelay());
        assertEquals(platform, readCheckPoint.getPlatform());

    }

}