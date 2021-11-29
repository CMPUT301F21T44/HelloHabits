package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class LoginTest {
    public static String NAME = "Test User";
    public static String EMAIL = "testuser@example.com";
    public static String PASSWORD = "testtestuseruser";

    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        TestUtil.initEmulators(sAuth, sDb);
    }

    @AfterClass
    public static void tearDown() {
        sAuth.signOut();
    }

    @Before
    public void logout() {
        sAuth.signOut();
    }

    @Test
    public void A_test_signup() {
        onView(withId(R.id.toggle)).perform(click());
        onView(withId(R.id.name)).perform(typeText(NAME), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.submit)).perform(click());
    }

    @Test
    public void B_test_login() {
        onView(withId(R.id.email)).perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.submit)).perform(click());
    }
}
