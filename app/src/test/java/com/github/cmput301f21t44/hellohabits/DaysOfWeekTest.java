package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
}
