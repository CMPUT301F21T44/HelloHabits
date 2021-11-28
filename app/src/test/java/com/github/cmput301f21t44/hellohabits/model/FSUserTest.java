package com.github.cmput301f21t44.hellohabits.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.cmput301f21t44.hellohabits.firebase.FSUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

/**
 * Test class for FSUser, the Firebase implementation of User interface
 */
public class FSUserTest {
    private static final String name = "Test User";
    private static final String email = "test_user@example.com";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Mock QueryDocumentSnapshot for creating an FSUser
     */
    @Mock
    QueryDocumentSnapshot mockDoc;

    /**
     * FSUser, the class being tested
     */
    private FSUser user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_fromDoc() {
        when(mockDoc.getId()).thenReturn(email);
        when(mockDoc.getString(FSUser.NAME)).thenReturn(name);

        user = new FSUser(mockDoc);
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
    }


    @Test
    public void test_getMap() {
        user = new FSUser(email, name);
        Map<String, Object> userMap = user.getMap();

        assertEquals(name, userMap.get(FSUser.NAME));
    }
}
