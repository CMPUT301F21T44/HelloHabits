package com.github.cmput301f21t44.hellohabits.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.firebase.FSFollow;
import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

/**
 * Test class for FSFollow, the Firebase implementation of Follow interface
 */
public class FSFollowTest {
    private static final String email = "test_user@example.com";
    private static final Follow.Status status = Follow.Status.ACCEPTED;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock QueryDocumentSnapshot for creating an FSFollow
     */
    @Mock
    QueryDocumentSnapshot mockDoc;

    /**
     * FSFollow, the class being tested
     */
    private FSFollow follow;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_fromDoc() {
        when(mockDoc.getId()).thenReturn(email);
        when(mockDoc.getString(FSFollow.STATUS)).thenReturn(status.getText());

        follow = new FSFollow(mockDoc);
        assertEquals(email, follow.getEmail());
        assertEquals(status, follow.getStatus());
    }


    @Test
    public void test_getMap() {
        follow = new FSFollow(email, status);
        Map<String, Object> followMap = follow.getMap();

        assertEquals(status.getText(), followMap.get(FSFollow.STATUS));
    }
}
