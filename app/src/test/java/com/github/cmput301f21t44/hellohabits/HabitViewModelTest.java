package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;
import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

@RunWith(JUnit4.class)
public class HabitViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    HabitRepository habitRepository;

    @Mock
    LiveData<List<Habit>> mockHabits;

    @Captor
    ArgumentCaptor<String> nameCaptor;
    @Captor
    ArgumentCaptor<String> reasonCaptor;
    @Captor
    ArgumentCaptor<Instant> dateStartedCaptor;
    @Captor
    ArgumentCaptor<boolean[]> daysOfWeekCaptor;
    @Captor
    ArgumentCaptor<FirebaseTask.ThenFunction> successCallbackCaptor;
    @Captor
    ArgumentCaptor<FirebaseTask.CatchFunction> failCallbackCaptor;

    private HabitViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(habitRepository.getAllHabits()).thenReturn(mockHabits);
        viewModel = new HabitViewModel(habitRepository);
    }

    @Test
    public void testGetHabits() {
        assertEquals(mockHabits, viewModel.getAllHabits());
    }

    @Test
    public void testInsert() {
        // Initialize method parameters
        String name = "Test Title";
        String reason = "Test Reason";
        Instant dateStarted = Instant.now();
        boolean[] daysOfWeek = DaysOfWeek.emptyArray();
        FirebaseTask.ThenFunction successCallback = () -> {
        };
        FirebaseTask.CatchFunction failCallback = (e) -> {
        };

        // Call method to test
        viewModel.insert(name, reason, dateStarted, daysOfWeek, successCallback, failCallback);

        // Capture values passed to the mock object
        verify(habitRepository, times(1)).insert(nameCaptor.capture(), reasonCaptor.capture(),
                dateStartedCaptor.capture(), daysOfWeekCaptor.capture(),
                successCallbackCaptor.capture(), failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(name, nameCaptor.getValue());
        assertEquals(reason, reasonCaptor.getValue());
        assertEquals(dateStarted, dateStartedCaptor.getValue());
        assertEquals(daysOfWeek, daysOfWeekCaptor.getValue());
        assertEquals(successCallback, successCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }
}
