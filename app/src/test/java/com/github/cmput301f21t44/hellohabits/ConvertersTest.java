package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;

import com.github.cmput301f21t44.hellohabits.db.Converters;
import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;

import org.junit.Test;

import java.time.Instant;

/* we don't care what value it gets converted into,
we only care that it still comes out the same when converted back */
public class ConvertersTest {
    @Test
    public void instantConverterTest() {
        Instant instant = Instant.now();
        String instantString = Converters.fromInstant(instant);
        assertEquals(instant, Converters.toInstant(instantString));
    }

    @Test
    public void boolArrayTest() {
        boolean[] boolArray = DaysOfWeek.emptyArray();
        // fiddle with a few bits
        for (int j : new int[]{0, 2, 6}) {
            boolArray[j] = true;
        }
        byte boolBitField = Converters.fromBoolArray(boolArray);
        boolean[] convertedArray = Converters.toBoolArray(boolBitField);
        for (int i = 0; i < boolArray.length; ++i) {
            assertEquals(boolArray[i], convertedArray[i]);
        }
    }
}
