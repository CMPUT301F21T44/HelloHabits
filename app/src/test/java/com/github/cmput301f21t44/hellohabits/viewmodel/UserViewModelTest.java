package com.github.cmput301f21t44.hellohabits.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Test class for the UserViewModel
 * <p>
 * Tests all methods of the UserViewModel except for the getters for MediatorLiveData members
 * as that is part of implementation (or bad design :P)
 */
@RunWith(JUnit4.class)
public class UserViewModelTest {
    // Initialize method parameters
    private static final String email = "test_user@example.com";
    private static final ThenFunction thenCallback = () -> {
    };
    private static final CatchFunction failCallback = (e) -> {
    };

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock UserRepository object to be injected to UserViewModel
     */
    @Mock
    UserRepository mockUserRepo;

    @Mock
    User mockUser;

    /**
     * LiveData stub to be returned by UserRepository.getAllUsers
     */
    @Mock
    LiveData<List<User>> userListStub;

    /**
     * Argument captors
     */
    @Captor
    ArgumentCaptor<String> emailCaptor;
    @Captor
    ArgumentCaptor<ThenFunction> thenCallbackCaptor;
    @Captor
    ArgumentCaptor<CatchFunction> failCallbackCaptor;

    /**
     * UserViewModel, the class being tested
     */
    private UserViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Provide stub value for getAllHabits
        when(mockUserRepo.getAllUsers()).thenReturn(userListStub);
        viewModel = new UserViewModel(mockUserRepo);
    }

    @Test
    public void test_isDefined() {
        assertNotNull(viewModel);
    }

    @Test
    public void test_selectedUser() {
        viewModel.setSelectedUser(mockUser);
        assertEquals(mockUser, viewModel.getSelectedUser().getValue());
    }

    @Test
    public void test_getAllUsers() {
        assertEquals(userListStub, viewModel.getAllUsers());
    }

    @Test
    public void test_requestFollow() {
        // Call method to test
        viewModel.requestFollow(email, thenCallback, failCallback);

        // Capture values passed to the mock object
        verify(mockUserRepo, times(1)).requestFollow(emailCaptor.capture(),
                thenCallbackCaptor.capture(), failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(email, emailCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_cancelFollowRequest() {
        // Call method to test
        viewModel.cancelFollowRequest(email, thenCallback, failCallback);

        // Capture values passed to the mock object
        verify(mockUserRepo, times(1)).cancelFollow(emailCaptor.capture(),
                thenCallbackCaptor.capture(), failCallbackCaptor.capture());

        // Verify that the mock method was called with the right parameters
        assertEquals(email, emailCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_acceptFollow() {
        viewModel.acceptFollow(email, thenCallback, failCallback);

        verify(mockUserRepo, times(1)).acceptFollow(emailCaptor.capture(),
                thenCallbackCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(email, emailCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }

    @Test
    public void test_rejectFollow() {
        viewModel.rejectFollow(email, thenCallback, failCallback);

        verify(mockUserRepo, times(1)).rejectFollow(emailCaptor.capture(),
                thenCallbackCaptor.capture(), failCallbackCaptor.capture());

        assertEquals(email, emailCaptor.getValue());
        assertEquals(thenCallback, thenCallbackCaptor.getValue());
        assertEquals(failCallback, failCallbackCaptor.getValue());
    }
}
