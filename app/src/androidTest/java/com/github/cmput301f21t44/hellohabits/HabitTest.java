package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class HabitTest {
    public static final String HABIT_TITLE = "Test Title";
    public static final String HABIT_REASON = "Test Reason";
    public static final String NEW_HABIT_TITLE = "New Test Title";
    public static final String NEW_HABIT_REASON = "New Test Reason";
    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void login() {
        TestUtil.initEmulators(sAuth, sDb);
        TestUtil.login(sAuth);
    }

    @Test
    public void A_test_addHabit() {
        onView(withId(R.id.button_new_habit)).perform(click());
        onView(withId(R.id.edit_text_title)).perform(typeText(HABIT_TITLE), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reason)).perform(typeText(HABIT_REASON), closeSoftKeyboard());

        onView(withId(R.id.reminder_layout)).perform(click());
        for (String s : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}) {
            onView(withText(s)).perform(click());
        }
        onView(withText("OK")).perform(click());

        onView(withId(R.id.private_checkbox)).perform(click());

        onView(withId(R.id.button_add_habit)).perform(click());
    }

    @Test
    public void B_test_viewHabit() {
        onView(withText(HABIT_TITLE)).perform(click());

        onView(withId(R.id.view_title)).check(matches(withText(HABIT_TITLE)));
        onView(withId(R.id.view_reason)).check(matches(withText(HABIT_REASON)));
        String everyDay = DaysOfWeek.toString(
                new boolean[]{true, true, true, true, true, true, true});

        onView(withId(R.id.view_privacy)).check(matches(withText(R.string.isPrivate)));
        onView(withId(R.id.view_reminder)).check(matches(withText(everyDay)));
    }

    @Test
    public void C_test_editHabit() {
        onView(withText(HABIT_TITLE)).perform(click());
        onView(withId(R.id.button_edit_habit)).perform(click());

        onView(withId(R.id.edit_text_title))
                .perform(clearText(), typeText(NEW_HABIT_TITLE), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reason))
                .perform(clearText(), typeText(NEW_HABIT_REASON), closeSoftKeyboard());

        onView(withId(R.id.reminder_layout)).perform(click());
        onView(withText("Sunday")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.private_checkbox)).perform(click());

        onView(withId(R.id.button_add_habit)).perform(click());

        onView(withId(R.id.view_title)).check(matches(withText(NEW_HABIT_TITLE)));
        onView(withId(R.id.view_reason)).check(matches(withText(NEW_HABIT_REASON)));
        String everyDay = DaysOfWeek.toString(
                new boolean[]{true, true, true, true, true, true, false});
        onView(withId(R.id.view_reminder)).check(matches(withText(everyDay)));
        onView(withId(R.id.view_privacy)).check(matches(withText(R.string.isPublic)));
    }

    @Test
    public void D_test_deleteHabit() {
        onView(withText(NEW_HABIT_TITLE)).perform(click());

        onView(withId(R.id.button_delete_habit)).perform(click());
        onView(withText("YES")).perform(click());
        onView(withText(NEW_HABIT_TITLE)).check(doesNotExist());
    }
}
