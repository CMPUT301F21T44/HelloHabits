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

    /**
     * Mock HabitRepository object to be injected to HabitViewModel
     */
    @Mock
    HabitRepository mockHabitRepo;

    /**
     * LiveData stub to be returned by HabitRepository.getAllHabits
     */
    @Mock
    LiveData<List<Habit>> habitListStub;

    /**
     * Argument captors
     */
    @Captor
    ArgumentCaptor<String> nameCaptor;
    @Captor
    ArgumentCaptor<String> reasonCaptor;
    @Captor
    ArgumentCaptor<Instant> dateStartedCaptor;
    @Captor
    ArgumentCaptor<boolean[]> daysOfWeekCaptor;
    @Captor
    ArgumentCaptor<Boolean> isPrivateCaptor;
    @Captor
    ArgumentCaptor<FirebaseTask.ThenFunction> successCallbackCaptor;
    @Captor
    ArgumentCaptor<FirebaseTask.CatchFunction> failCallbackCaptor;

    /**
     * HabitViewModel, the class being tested
     */
    private HabitViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Provide stub value for getAllHabits
        when(mockHabitRepo.getAllHabits()).thenReturn(habitListStub);
        viewModel = new HabitViewModel(mockHabitRepo);
    }

    @Test
    public void test_getAllHabits() {
        // Verify that getAllHabits returns the stub
        assertEquals(habitListStub, viewModel.getAllHabits());
    }

    @Test
    public void test_insert() {
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
        viewModel.insert(name, reason, dateStarted, daysOfWeek, true, successCallback,
                failCallback);

        // Capture values passed to the mock object
        verify(mockHabitRepo, times(1)).insert(nameCaptor.capture(), reasonCaptor.capture(),
                dateStartedCaptor.capture(), daysOfWeekCaptor.capture(), isPrivateCaptor.capture(),
                successCallbackCaptor.capture(), failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(name, nameCaptor.getValue());
        assertEquals(reason, reasonCaptor.getValue());
        assertEquals(dateStarted, dateStartedCaptor.getValue());
        assertEquals(daysOfWeek, daysOfWeekCaptor.getValue());
        assertEquals(true, isPrivateCaptor.getValue());
        assertEquals(successCallback, successCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }
}
