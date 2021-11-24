package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;

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
 * Test class for the HabitEventViewModel
 * <p>
 * Tests all methods of the HabitEventViewModel
 */
@RunWith(JUnit4.class)
public class HabitEventViewModelTest {
    // Initialize method parameters
    private static final String id = "1944ce84-3b81-4763-9ff7-26aa48eb8f81";
    private static final String habitId = "85c92132-3977-4b2e-84e5-f97298a69885";
    private static final String comment = "Test Comment";
    private static final Instant date = Instant.now();
    private static final ThenFunction thenCallback = () -> {
    };
    private static final ResultFunction<HabitEvent> resultCallback = (h) -> {
    };
    private static final CatchFunction failCallback = (e) -> {
    };

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock HabitEventRepository object to be injected to HabitEventViewModel
     */
    @Mock
    HabitEventRepository mockHabitEventRepo;

    @Mock
    HabitEvent mockHabitEvent;

    /**
     * LiveData stub to be returned by HabitEventRepository.getEventsByHabitId
     */
    @Mock
    LiveData<List<HabitEvent>> habitEventListStub;

    /**
     * Argument captors
     */
    @Captor
    ArgumentCaptor<String> idCaptor;
    @Captor
    ArgumentCaptor<String> habitIdCaptor;
    @Captor
    ArgumentCaptor<String> commentCaptor;
    @Captor
    ArgumentCaptor<Instant> dateCaptor;
    @Captor
    ArgumentCaptor<ThenFunction> thenCallbackCaptor;
    @Captor
    ArgumentCaptor<ResultFunction<HabitEvent>> resultCallbackCaptor;
    @Captor
    ArgumentCaptor<CatchFunction> failCallbackCaptor;
    @Captor
    ArgumentCaptor<HabitEvent> habitEventCaptor;

    /**
     * HabitEventViewModel, the class being tested
     */
    private HabitEventViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Provide stub value for getAllHabits
        when(mockHabitEventRepo.getEventsByHabitId(habitId)).thenReturn(habitEventListStub);
        viewModel = new HabitEventViewModel(mockHabitEventRepo);
    }

    @Test
    public void test_isDefined() {
        assertNotNull(viewModel);
    }

    @Test
    public void test_selectedEvent() {
        viewModel.setSelectedEvent(mockHabitEvent);
        assertEquals(mockHabitEvent, viewModel.getSelectedEvent().getValue());
    }

    @Test
    public void test_getEventsByHabitId() {
        assertEquals(habitEventListStub, viewModel.getHabitEventsById(habitId));
    }

    @Test
    public void test_insert() {
        // Call method to test
        viewModel.insert(habitId, comment, thenCallback, failCallback);

        // Capture values passed to the mock object
        verify(mockHabitEventRepo, times(1)).insert(habitIdCaptor.capture(),
                commentCaptor.capture(), thenCallbackCaptor.capture(),
                failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(habitId, habitIdCaptor.getValue());
        assertEquals(comment, commentCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_update() {
        viewModel.update(id, habitId, date, comment, resultCallback, failCallback);

        verify(mockHabitEventRepo, times(1)).update(idCaptor.capture(),
                habitIdCaptor.capture(), dateCaptor.capture(), commentCaptor.capture(),
                resultCallbackCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(id, idCaptor.getValue());
        assertEquals(habitId, habitIdCaptor.getValue());
        assertEquals(date, dateCaptor.getValue());
        assertEquals(comment, commentCaptor.getValue());
        assertEquals(resultCallback, resultCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_delete() {
        viewModel.delete(mockHabitEvent, failCallback);

        verify(mockHabitEventRepo, times(1))
                .delete(habitEventCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(mockHabitEvent, habitEventCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }
}
