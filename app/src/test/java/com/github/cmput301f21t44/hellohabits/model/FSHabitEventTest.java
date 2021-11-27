package com.github.cmput301f21t44.hellohabits.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.firebase.FSHabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Map;

/**
 * Test class for FSHabitEvent, the Firebase implementation of HabitEvent interface
 */
public class FSHabitEventTest {
    private static final String id = "1944ce84-3b81-4763-9ff7-26aa48eb8f81";
    private static final String habitId = "85c92132-3977-4b2e-84e5-f97298a69885";
    private static final String comment = "Test Comment";
    private static final Instant date = Instant.now();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock QueryDocumentSnapshot for creating an FSHabitEvent
     */
    @Mock
    QueryDocumentSnapshot mockDoc;

    /**
     * Mock Habit for creating an FSHabitEvent
     */
    @Mock
    HabitEvent mockHabitEvent;

    /**
     * FSHabitEvent, the class being tested
     */
    private FSHabitEvent habitEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_fromDoc() {
        when(mockDoc.getId()).thenReturn(id);
        when(mockDoc.getString(FSHabitEvent.HABIT_ID)).thenReturn(habitId);
        when(mockDoc.getString(FSHabitEvent.COMMENT)).thenReturn(comment);
        when(mockDoc.getLong(FSHabitEvent.DATE + ".epochSecond"))
                .thenReturn(date.getEpochSecond());
        when(mockDoc.getLong(FSHabitEvent.DATE + ".nano"))
                .thenReturn((long) date.getNano());

        habitEvent = new FSHabitEvent(mockDoc);
        assertCorrectHabitEvent();
    }

    @Test
    public void test_fromHabitEvent() {
        when(mockHabitEvent.getId()).thenReturn(id);
        when(mockHabitEvent.getHabitId()).thenReturn(habitId);
        when(mockHabitEvent.getDate()).thenReturn(date);
        when(mockHabitEvent.getComment()).thenReturn(comment);

        habitEvent = new FSHabitEvent(mockHabitEvent);
        assertCorrectHabitEvent();
    }

    private void assertCorrectHabitEvent() {
        assertEquals(id, habitEvent.getId());
        assertEquals(habitId, habitEvent.getHabitId());
        assertEquals(comment, habitEvent.getComment());
        assertEquals(date, habitEvent.getDate());
    }

    @Test
    public void test_getMap() {
        habitEvent = new FSHabitEvent(id, date, habitId, comment);
        Map<String, Object> habitEventMap = habitEvent.getMap();

        assertEquals(date, habitEventMap.get(FSHabitEvent.DATE));
        assertEquals(habitId, habitEventMap.get(FSHabitEvent.HABIT_ID));
        assertEquals(comment, habitEventMap.get(FSHabitEvent.COMMENT));
    }
}
