package com.github.cmput301f21t44.hellohabits;

import static org.junit.Assert.assertNotNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;
import com.github.cmput301f21t44.hellohabits.model.UserRepository;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class ViewModelFactoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock HabitRepository object to be injected to ViewModelFactory
     */
    @Mock
    HabitRepository mockHabitRepo;

    /**
     * Mock HabitEventRepository object to be injected to ViewModelFactory
     */
    @Mock
    HabitEventRepository mockHabitEventRepo;

    /**
     * Mock UserRepository object to be injected to ViewModelFactory
     */
    @Mock
    UserRepository mockUserRepo;

    private ViewModelFactory viewModelFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModelFactory = new ViewModelFactory(mockHabitRepo, mockHabitEventRepo, mockUserRepo);
    }

    @Test
    public void test_isDefined() {
        assertNotNull(viewModelFactory);
    }

    @Test
    public void test_createViewModels() {
        assertNotNull(viewModelFactory.create(HabitViewModel.class));
        assertNotNull(viewModelFactory.create(HabitEventViewModel.class));
        assertNotNull(viewModelFactory.create(PreviousListViewModel.class));
        assertNotNull(viewModelFactory.create(UserViewModel.class));
    }
}
