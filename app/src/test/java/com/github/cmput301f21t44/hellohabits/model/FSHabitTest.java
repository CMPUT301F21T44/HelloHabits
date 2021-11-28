package com.github.cmput301f21t44.hellohabits.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.firebase.FSHabit;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Test class for FSHabit, the Firebase implementation of Habit interface
 */
@RunWith(JUnit4.class)
public class FSHabitTest {
    // Initialize method parameters
    private static final String id = "1944ce84-3b81-4763-9ff7-26aa48eb8f81";
    private static final String title = "Test Title";
    private static final String reason = "Test Reason";
    private static final Instant dateStarted = Instant.now();
    private static final boolean[] daysOfWeek = DaysOfWeek.emptyArray();
    private static final int index = 1;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock QueryDocumentSnapshot for creating an FSHabit
     */
    @Mock
    QueryDocumentSnapshot mockDoc;

    /**
     * Mock Habit for creating an FSHabit
     */
    @Mock
    Habit mockHabit;

    /**
     * FSHabit, the class being tested
     */
    private FSHabit habit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_fromDoc() {
        when(mockDoc.getId()).thenReturn(id);
        when(mockDoc.getString(FSHabit.TITLE)).thenReturn(title);
        when(mockDoc.getString(FSHabit.REASON)).thenReturn(reason);
        when(mockDoc.getLong(FSHabit.DATE_STARTED + ".epochSecond"))
                .thenReturn(dateStarted.getEpochSecond());
        when(mockDoc.getLong(FSHabit.DATE_STARTED + ".nano"))
                .thenReturn((long) dateStarted.getNano());
        when(mockDoc.get(FSHabit.DAYS_OF_WEEK)).thenReturn(DaysOfWeek.toList(daysOfWeek));
        when(mockDoc.getBoolean(FSHabit.IS_PRIVATE)).thenReturn(true);
        when(mockDoc.getLong(FSHabit.HABIT_INDEX)).thenReturn((long) index);

        habit = new FSHabit(mockDoc);
        assertCorrectHabit();
    }

    @Test
    public void test_fromHabit() {
        when(mockHabit.getId()).thenReturn(id);
        when(mockHabit.getTitle()).thenReturn(title);
        when(mockHabit.getReason()).thenReturn(reason);
        when(mockHabit.getDateStarted()).thenReturn(dateStarted);
        when(mockHabit.getDaysOfWeek()).thenReturn(daysOfWeek);
        when(mockHabit.isPrivate()).thenReturn(true);
        when(mockHabit.getIndex()).thenReturn(index);

        habit = new FSHabit(mockHabit);
        assertCorrectHabit();
    }

    private void assertCorrectHabit() {
        assertEquals(id, habit.getId());
        assertEquals(title, habit.getTitle());
        assertEquals(reason, habit.getReason());
        assertEquals(dateStarted, habit.getDateStarted());
        compareDaysOfWeek(habit.getDaysOfWeek());
        assertTrue(habit.isPrivate());
        assertEquals(index, habit.getIndex());
    }

    private void compareDaysOfWeek(boolean[] habitDaysOfWeek) {
        assert daysOfWeek.length == habitDaysOfWeek.length;
        for (int i = 0; i < daysOfWeek.length; ++i) {
            assertEquals(daysOfWeek[i], habitDaysOfWeek[i]);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_getMap() {
        habit = new FSHabit(id, title, reason, dateStarted, daysOfWeek, true, index);
        Map<String, Object> habitMap = habit.getMap();

        assertEquals(title, habitMap.get(FSHabit.TITLE));
        assertEquals(reason, habitMap.get(FSHabit.REASON));
        assertEquals(dateStarted, habitMap.get(FSHabit.DATE_STARTED));
        compareDaysOfWeek(DaysOfWeek.fromList(
                (List<Boolean>) Objects.requireNonNull(habitMap.get(FSHabit.DAYS_OF_WEEK))));
        assertEquals(true, habitMap.get(FSHabit.IS_PRIVATE));
        assertEquals(index, habitMap.get(FSHabit.HABIT_INDEX));
    }
}
