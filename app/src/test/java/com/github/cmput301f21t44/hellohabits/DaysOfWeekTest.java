package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class DaysOfWeekTest {
    @Test
    public void test_toString_emptyArray() {
        boolean[] daysOfWeek = DaysOfWeek.emptyArray();
        assertTrue(DaysOfWeek.toString(daysOfWeek).isEmpty());
    }

    @Test
    public void test_toString_oneDay() {
        for (int i = 0; i < DaysOfWeek.shorthandDays.length; ++i) {
            boolean[] daysOfWeek = DaysOfWeek.emptyArray();
            daysOfWeek[i] = true;
            assertEquals(DaysOfWeek.shorthandDays[i], DaysOfWeek.toString(daysOfWeek));
        }
    }

    @Test
    public void test_toString_twoDays() {
        for (int i = 0; i < DaysOfWeek.shorthandDays.length - 1; ++i) {
            boolean[] daysOfWeek = DaysOfWeek.emptyArray();
            daysOfWeek[i] = true;
            daysOfWeek[i + 1] = true;
            assertEquals(
                    DaysOfWeek.shorthandDays[i] + " " + DaysOfWeek.shorthandDays[i + 1],
                    DaysOfWeek.toString(daysOfWeek));
        }
    }

    @Test
    public void test_toFromList() {
        boolean[] daysOfWeek = DaysOfWeek.emptyArray();
        daysOfWeek[3] = true;
        // we don't care how it's stored in the List, as long as the values stay the same when
        // converted back to an array
        List<Boolean> daysList = DaysOfWeek.toList(daysOfWeek);
        boolean[] convertedArray = DaysOfWeek.fromList(daysList);
        for (int i = 0; i < daysOfWeek.length; ++i) {
            assertEquals(daysOfWeek[i], convertedArray[i]);
        }
    }
}
