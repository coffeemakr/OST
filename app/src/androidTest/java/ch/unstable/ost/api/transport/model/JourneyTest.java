package ch.unstable.ost.api.transport.model;

import android.os.Parcel;

import org.junit.Test;

import ch.unstable.ost.utils.ParcelCompat;

import static ch.unstable.ost.api.transport.model.CapacityTest.writeAndRead;
import static org.junit.Assert.*;

public class JourneyTest {
    @Test
    public void writeToParcel() throws Exception {
        String name = null;
        String category = null;
        String categoryCode = null;
        int number = 0;
        String operator = null;
        String to = null;
        Capacity capacity = null;

        Journey journey = new Journey(name, category, categoryCode, number, operator, to, capacity, passList);

        Journey readJourney = writeAndRead(journey, Journey.CREATOR);

        assertNull(readJourney.getName());
        assertNull(readJourney.getCategory());
        assertNull(readJourney.getCategoryCode());
        assertEquals(0, readJourney.getNumber());
        assertNull(readJourney.getOperator());
        assertNull(readJourney.getTo());
        assertNull(readJourney.getCapacity());


        name = "journey name";
        category = "category";
        categoryCode = "e29w1q03";
        number = 321;
        operator = "OPWER";
        to = "to";
        capacity = new Capacity(10, 2);

        journey = new Journey(name, category, categoryCode, number, operator, to, capacity, passList);

        readJourney = writeAndRead(journey, Journey.CREATOR);


        assertEquals(name, readJourney.getName());
        assertEquals(category, readJourney.getCategory());
        assertEquals(categoryCode, readJourney.getCategoryCode());
        assertEquals(number, readJourney.getNumber());
        assertEquals(operator, readJourney.getOperator());
        assertEquals(to, readJourney.getTo());
        assertEquals(capacity, readJourney.getCapacity());


        Parcel parcel = Parcel.obtain();
        ParcelCompat.writeTypeObject(parcel, journey, 0);
        parcel.setDataPosition(0);
        readJourney = ParcelCompat.readTypeObject(parcel, Journey.CREATOR);

        assertNotNull(readJourney);
        assertEquals(name, readJourney.getName());
        assertEquals(category, readJourney.getCategory());
        assertEquals(categoryCode, readJourney.getCategoryCode());
        assertEquals(number, readJourney.getNumber());
        assertEquals(operator, readJourney.getOperator());
        assertEquals(to, readJourney.getTo());
        assertEquals(capacity, readJourney.getCapacity());
    }

}