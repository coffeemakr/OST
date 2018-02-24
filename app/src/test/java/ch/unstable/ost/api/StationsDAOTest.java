package ch.unstable.ost.api;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.search.SearchAPI;
import ch.unstable.ost.api.transport.TransportAPI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class StationsDAOTest {

    private StationsDAO stationsDAO;

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(
                new Object[]{new SearchAPI()},
                new Object[]{new TransportAPI()}
        );
    }

    public StationsDAOTest(StationsDAO stationsDAO) {
        this.stationsDAO = stationsDAO;
    }

    @Test
    public void getStationsByQuery() throws Exception {
        Location location;
        Location[] locations = stationsDAO.getStationsByQuery("luz");
        assertDoesNotContain(null, locations);

        location = findLocationByName(locations, "Luzern");
        assertEquals("8505000", location.getId());
        /*
        if(!(stationsDAO instanceof TransportAPI)) {
            assertEquals(location.getName(), Location.StationType.TRAIN, location.getType());
        }
        */
        assertEquals("Luzern is not first place", locations[0], location);

        location = findLocationByName(locations, "Luzern, Kantonalbank");
        assertEquals("8589801", location.getId());
        /*
        if(!(stationsDAO instanceof TransportAPI)) {
            assertEquals(Location.StationType.BUS, location.getType());
        }
        */
    }

    public static <E> void assertDoesNotContain(E itemNotToContain, E[] items) {
        for (E item : items) {
            if (item.equals(itemNotToContain)) {
                fail("Array does contain item " + itemNotToContain);
            }
        }
    }


    @NonNull
    private static Location findLocationByName(Location[] locations, String name) {
        for (Location location : locations) {
            if (location.getName().equals(name)) {
                return location;
            }
        }
        throw new AssertionError("locations don't contain location with name " + name);
    }
}
