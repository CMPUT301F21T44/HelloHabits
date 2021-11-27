package com.github.cmput301f21t44.hellohabits.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PreviousListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PreviousListViewModel previousListViewModel;

    @Before
    public void setup() {
        previousListViewModel = new PreviousListViewModel();
    }

    @Test
    public void test_isDefined() {
        assertNotNull(previousListViewModel);
        //noinspection ConstantConditions
        Assert.assertEquals(R.id.TodaysHabitsFragment, (long) previousListViewModel.getDestinationId().getValue());
        //noinspection ConstantConditions
        assertFalse(previousListViewModel.getFromViewHabit().getValue());
    }

    @Test
    public void test_destinationId() {
        int id = R.id.AllHabitsFragment;
        previousListViewModel.setDestinationId(id);
        //noinspection ConstantConditions
        assertEquals(id, (long) previousListViewModel.getDestinationId().getValue());
    }

    @Test
    public void test_fromViewHabit() {
        previousListViewModel.setFromViewHabit(true);
        //noinspection ConstantConditions
        assertTrue(previousListViewModel.getFromViewHabit().getValue());
    }
}
