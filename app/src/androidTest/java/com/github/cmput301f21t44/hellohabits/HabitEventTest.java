package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.github.cmput301f21t44.hellohabits.firebase.FSHabit;
import com.github.cmput301f21t44.hellohabits.firebase.User;
import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class HabitEventTest {
    public static final String EVENT_COMMENT = "Test Comment";
    public static final String NEW_EVENT_COMMENT = "New Test Comment";
    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    private static String habitId;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @BeforeClass
    public static void setup() {
        final FSHabit habit = new FSHabit(UUID.randomUUID().toString(), HabitTest.HABIT_TITLE,
                HabitTest.HABIT_REASON, Instant.now(), DaysOfWeek.emptyArray());
        TestUtil.initEmulators(sAuth, sDb);
        sAuth.signOut();
        TestUtil.login(sAuth);
        AtomicBoolean hasHabitEvent = new AtomicBoolean(false);
        habitId = habit.getId();
        sDb.collection(User.COLLECTION).document(LoginTest.EMAIL).collection(FSHabit.COLLECTION)
                .document(habit.getId()).set(FSHabit.getMap(habit)).addOnSuccessListener(u -> {
            hasHabitEvent.set(true);
        });
        while (!hasHabitEvent.get()) {
            SystemClock.sleep(100);
        }
    }

    @AfterClass
    public static void tearDown() {
        sDb.collection(User.COLLECTION).document(LoginTest.EMAIL).collection(FSHabit.COLLECTION)
                .document(habitId).delete();
    }


    @Test
    public void A_test_addEvent() {
        onView(withId(R.id.view_all_habits)).perform(click());
        onView(withText(HabitTest.HABIT_TITLE)).perform(click());
        onView(withId(R.id.button_new_habit_event)).perform(click());
        onView(withId(R.id.edit_text_comment)).perform(typeText(EVENT_COMMENT), closeSoftKeyboard());
        onView(withId(R.id.button_add_habit_event)).perform(click());
    }

    @Test
    public void B_test_viewEvent() {
        onView(withId(R.id.view_all_habits)).perform(click());
        onView(withText(HabitTest.HABIT_TITLE)).perform(click());
        onView(withText(EVENT_COMMENT)).check(matches(isDisplayed()));
    }

    @Test
    public void C_test_editEvent() {
        onView(withId(R.id.view_all_habits)).perform(click());
        onView(withText(HabitTest.HABIT_TITLE)).perform(click());
        onView(withId(R.id.button_edit)).perform(click());
        onView(withId(R.id.edit_text_comment))
                .perform(clearText(), typeText(NEW_EVENT_COMMENT), closeSoftKeyboard());
        onView(withId(R.id.button_add_habit_event)).perform(click());
        onView(withText(NEW_EVENT_COMMENT)).check(matches(isDisplayed()));
    }

    @Test
    public void D_test_deleteEvent() {
        onView(withId(R.id.view_all_habits)).perform(click());
        onView(withText(HabitTest.HABIT_TITLE)).perform(click());
        onView(withId(R.id.button_delete)).perform(click());
        onView(withText("YES")).perform(click());
        onView(withText(NEW_EVENT_COMMENT)).check(doesNotExist());
    }
}
