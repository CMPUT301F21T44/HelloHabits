package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.model.habit.HabitRepository;
import com.github.cmput301f21t44.hellohabits.view.habit.HabitIndexChange;
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

/**
 * Test class for the HabitViewModel
 * <p>
 * Tests all methods of the HabitViewModel
 */
@RunWith(JUnit4.class)
public class HabitViewModelTest {
    // Initialize method parameters
    private static final String id = "1944ce84-3b81-4763-9ff7-26aa48eb8f81";
    private static final String name = "Test Title";
    private static final String email = "test_user@example.com";
    private static final String reason = "Test Reason";
    private static final Instant dateStarted = Instant.now();
    private static final boolean[] daysOfWeek = DaysOfWeek.emptyArray();
    private static final int index = 1;
    private static final ThenFunction thenCallback = () -> {
    };
    private static final ResultFunction<Habit> resultCallback = (h) -> {
    };
    private static final CatchFunction failCallback = (e) -> {
    };

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock HabitRepository object to be injected to HabitViewModel
     */
    @Mock
    HabitRepository mockHabitRepo;

    @Mock
    Habit mockHabit;

    @Mock
    List<HabitIndexChange> mockChangeList;

    /**
     * LiveData stub to be returned by HabitRepository.getAllHabits
     */
    @Mock
    LiveData<List<Habit>> habitListStub;

    /**
     * Argument captors
     */
    @Captor
    ArgumentCaptor<String> idCaptor;
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
    ArgumentCaptor<Integer> indexCaptor;
    @Captor
    ArgumentCaptor<ThenFunction> thenCallbackCaptor;
    @Captor
    ArgumentCaptor<ResultFunction<Habit>> resultCallbackCaptor;
    @Captor
    ArgumentCaptor<CatchFunction> failCallbackCaptor;
    @Captor
    ArgumentCaptor<Habit> habitCaptor;
    @Captor
    ArgumentCaptor<List<HabitIndexChange>> changeListCaptor;

    /**
     * HabitViewModel, the class being tested
     */
    private HabitViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Provide stub value for getAllHabits
        when(mockHabitRepo.getAllHabits()).thenReturn(habitListStub);
        when(mockHabitRepo.getUserPublicHabits(email)).thenReturn(habitListStub);
        viewModel = new HabitViewModel(mockHabitRepo);
    }

    @Test
    public void test_isDefined() {
        assertNotNull(viewModel);
    }

    @Test
    public void test_selectedHabit() {
        viewModel.setSelectedHabit(mockHabit);
        assertEquals(mockHabit, viewModel.getSelectedHabit().getValue());
    }

    @Test
    public void test_reordering() {
        viewModel.setReordering(true);
        //noinspection ConstantConditions
        assertTrue(viewModel.getReordering().getValue());
    }

    @Test
    public void test_getAllHabits() {
        // Verify that getAllHabits returns the stub
        assertEquals(habitListStub, viewModel.getAllHabits());
    }

    @Test
    public void test_getUserPublicHabits() {
        assertEquals(habitListStub, viewModel.getUserPublicHabits(email));
    }

    @Test
    public void test_insert() {
        // Call method to test
        viewModel.insert(name, reason, dateStarted, daysOfWeek, true, thenCallback,
                failCallback);

        // Capture values passed to the mock object
        verify(mockHabitRepo, times(1)).insert(nameCaptor.capture(),
                reasonCaptor.capture(), dateStartedCaptor.capture(), daysOfWeekCaptor.capture(),
                isPrivateCaptor.capture(), thenCallbackCaptor.capture(),
                failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(name, nameCaptor.getValue());
        assertEquals(reason, reasonCaptor.getValue());
        assertEquals(dateStarted, dateStartedCaptor.getValue());
        assertEquals(daysOfWeek, daysOfWeekCaptor.getValue());
        assertEquals(true, isPrivateCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_update() {
        viewModel.update(id, name, reason, dateStarted, daysOfWeek, true, index,
                resultCallback, failCallback);

        verify(mockHabitRepo, times(1)).update(idCaptor.capture(),
                nameCaptor.capture(), reasonCaptor.capture(), dateStartedCaptor.capture(),
                daysOfWeekCaptor.capture(), isPrivateCaptor.capture(), indexCaptor.capture(),
                resultCallbackCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(id, idCaptor.getValue());
        assertEquals(name, nameCaptor.getValue());
        assertEquals(reason, reasonCaptor.getValue());
        assertEquals(dateStarted, dateStartedCaptor.getValue());
        assertEquals(daysOfWeek, daysOfWeekCaptor.getValue());
        assertEquals(true, isPrivateCaptor.getValue());
        assertEquals(index, (int) indexCaptor.getValue());
        assertEquals(resultCallback, resultCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_delete() {
        viewModel.delete(mockHabit, thenCallback, failCallback);

        verify(mockHabitRepo, times(1)).delete(habitCaptor.capture(),
                thenCallbackCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(mockHabit, habitCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_updateIndices() {
        viewModel.updateIndices(mockChangeList, failCallback);

        verify(mockHabitRepo, times(1))
                .updateIndices(changeListCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(mockChangeList, changeListCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }
}
