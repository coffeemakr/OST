package ch.unstable.ost.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ObjectsCompatTest {
    @Test
    public void requireNonNull() throws Exception {
        String valueBefore = "Something";
        String value = ObjectsCompat.requireNonNull(valueBefore, "value");
        assertSame(valueBefore, value);

        try {
            ObjectsCompat.requireNonNull(null, "value");
            fail("no exception thrown");
        } catch (NullPointerException e) {
            assertEquals("value is null", e.getMessage());
        }

        try {
            ObjectsCompat.requireNonNull(null, "something else");
            fail("no exception thrown");
        } catch (NullPointerException e) {
            assertEquals("something else is null", e.getMessage());
        }
    }

    @Test
    public void requireNonEmpty() throws Exception {
        try {
            ObjectsCompat.requireNonEmpty(null, "null");
            fail("no exception thrown");
        } catch (NullPointerException e) {
            assertEquals("null is null", e.getMessage());
        }

        try {
            ObjectsCompat.requireNonEmpty("", "Value");
            fail("no exception thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Value must not be empty", e.getMessage());
        }

        String correctValue = "something";
        assertSame(correctValue, ObjectsCompat.requireNonEmpty(correctValue, "value"));
    }

}