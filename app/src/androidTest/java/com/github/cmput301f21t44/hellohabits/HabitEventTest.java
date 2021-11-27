package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment.MAX_TITLE_LEN;
import static com.github.cmput301f21t44.hellohabits.view.habitevent.CreateEditHabitEventFragment.MAX_COMMENT_LEN;

import android.os.SystemClock;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.cmput301f21t44.hellohabits.firebase.FSHabit;
import com.github.cmput301f21t44.hellohabits.firebase.FSUser;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
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
    private static String habitTitle;
    private static String newComment;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @BeforeClass
    public static void setup() {
        habitTitle = TestUtil.getRealTimeString(HabitTest.HABIT_TITLE, MAX_TITLE_LEN);
        newComment = TestUtil.getRealTimeString(NEW_EVENT_COMMENT, MAX_COMMENT_LEN);
        final FSHabit habit = new FSHabit(UUID.randomUUID().toString(), habitTitle,
                HabitTest.HABIT_REASON, Instant.now(), DaysOfWeek.emptyArray(), true, 0);
        TestUtil.initEmulators(sAuth, sDb);
        sAuth.signOut();
        TestUtil.login(sAuth);
        AtomicBoolean hasHabit = new AtomicBoolean(false);
        habitId = habit.getId();
        sDb.collection(FSUser.COLLECTION).document(LoginTest.EMAIL).collection(FSHabit.COLLECTION)
                .document(habit.getId()).set(habit.getMap()).addOnSuccessListener(u ->
                hasHabit.set(true)).addOnFailureListener(e -> hasHabit.set(true));

        while (!hasHabit.get()) {
            SystemClock.sleep(100);
        }
    }

    @AfterClass
    public static void tearDown() {
        sDb.collection(FSUser.COLLECTION).document(LoginTest.EMAIL).collection(FSHabit.COLLECTION)
                .document(habitId).delete();
    }

    private void navigateToHabit() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.AllHabitsFragment));
        onView(withText(habitTitle)).perform(doubleClick());
    }


    @Test
    public void A_test_addEvent() {
        navigateToHabit();
        onView(withId(R.id.button_new_habit_event)).perform(click());
        onView(withId(R.id.edit_text_comment)).perform(typeText(EVENT_COMMENT), closeSoftKeyboard());
        onView(withId(R.id.button_add_habit_event)).perform(click());
    }

    @Test
    public void B_test_viewEvent() {
        navigateToHabit();
        onView(withText(EVENT_COMMENT)).check(matches(isDisplayed()));
    }

    @Test
    public void C_test_editEvent() {
        navigateToHabit();
        onView(withId(R.id.button_edit)).perform(click());
        onView(withId(R.id.edit_text_comment))
                .perform(clearText(), typeText(newComment), closeSoftKeyboard());
        onView(withId(R.id.button_add_habit_event)).perform(click());
        onView(withText(newComment)).check(matches(isDisplayed()));
    }

    @Test
    public void D_test_deleteEvent() {
        navigateToHabit();
        onView(withId(R.id.button_delete)).perform(click());
        onView(withText("YES")).perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withText(newComment)).check(doesNotExist());
    }
}
