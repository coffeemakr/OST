package ch.unstable.ost.api.transport.model;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

import static ch.unstable.ost.api.transport.model.CapacityTest.writeAndRead;
import static org.junit.Assert.*;


public class SectionTest {

    public static Checkpoint generateRandomCheckpoint() {
        OSLocation station = generateRandomLocation();
        Random random = new Random();
        long date1 = random.nextLong();
        long date2 = random.nextLong();

        Date arrival = new Date(date1);
        Date departureTime = new Date(date2);
        Integer delay = random.nextInt();
        String platform = "" + random.nextInt();
        return new Checkpoint(station, arrival, departureTime, delay, platform);
    }

    private static <T> T getRandomValue(T[] values) {
        Random random = new Random();
        int i = random.nextInt(values.length);
        return values[i];
    }

    private static OSLocation generateRandomLocation() {
        Random random = new Random();
        String id = "idsaoja" + random.nextInt();
        OSLocation.Type type = getRandomValue(OSLocation.Type.values());
        String name = "name" + random.nextInt();
        Coordinates coordinates = new Coordinates(random.nextDouble(), random.nextDouble());
        return new OSLocation(id, type, name, coordinates);
    }

    @Test
    public void writeToParcel() throws Exception {

        String name = "journey_name";
        String category = "journey_category";
        String categoryCode = "10";
        int number = 213;
        String operator = "SBB";
        String to = "Zürich HB";
        Capacity capacity = new Capacity(10, 10);
        Checkpoint[] passList = new Checkpoint[0];
        Journey journey = new Journey(name, category, categoryCode, number, operator, to, capacity, passList);

        Checkpoint arrival = generateRandomCheckpoint();
        Checkpoint departure = generateRandomCheckpoint();

        Walk walk = null;

        Section section = new Section(departure, arrival, journey, walk);
        assertEquals(section, section);
        assertEquals(departure, section.getDeparture());
        assertEquals(arrival, section.getArrival());

        Section readSection = writeAndRead(section, Section.CREATOR);
        assertEquals(journey, readSection.getJourney());
        assertEquals(departure, readSection.getDeparture());
        assertEquals(arrival, readSection.getArrival());
        assertEquals(section, readSection);
    }

}