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
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment.MAX_TITLE_LEN;
import static org.hamcrest.Matchers.allOf;

import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.cmput301f21t44.hellohabits.firebase.FSHabit;
import com.github.cmput301f21t44.hellohabits.firebase.FirestoreHabitRepository;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.HabitRepository;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class HabitTest {
    public static final String HABIT_TITLE = "Test Title";
    public static final String HABIT_REASON = "Test Reason";
    public static final String NEW_HABIT_TITLE = "New Test Title";
    public static final String NEW_HABIT_REASON = "New Test Reason";
    public static final String REORDER_TITLE_1 = "Reorder Title 1";
    public static final String REORDER_TITLE_2 = "Reorder Title 2";
    public static final String uuid1 = "d500d007-600c-4a06-9a6b-c69ba7638c65";
    public static final String uuid2 = "8d5dd988-7333-42da-bb21-1f3577acc1f0";
    private static final String newHabitTitle = TestUtil.getRealTimeString(NEW_HABIT_TITLE, MAX_TITLE_LEN);
    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static HabitRepository sHabitRepo = new FirestoreHabitRepository(sDb, sAuth);
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void login() {
        TestUtil.initEmulators(sAuth, sDb);
        TestUtil.login(sAuth);
    }

    // https://stackoverflow.com/questions/24748303/selecting-child-view-at-index-using-espresso
    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + childPosition + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) &&
                        group.getChildAt(childPosition).equals(view);
            }
        };
    }

    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
                view -> GeneralLocation.TOP_CENTER.calculateCoordinates((View) view.getParent().getParent()), Press.FINGER);
    }

    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                view -> GeneralLocation.BOTTOM_CENTER.calculateCoordinates((View) view.getParent().getParent()), Press.FINGER);
    }

    @Test
    public void A_test_addHabit() {
        onView(withId(R.id.new_habit)).perform(click());
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
                .perform(clearText(), typeText(newHabitTitle), closeSoftKeyboard());
        onView(withId(R.id.edit_text_reason))
                .perform(clearText(), typeText(NEW_HABIT_REASON), closeSoftKeyboard());

        onView(withId(R.id.reminder_layout)).perform(click());
        onView(withText("Sunday")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.private_checkbox)).perform(click());

        onView(withId(R.id.button_add_habit)).perform(click());

        onView(withId(R.id.view_title)).check(matches(withText(newHabitTitle)));
        onView(withId(R.id.view_reason)).check(matches(withText(NEW_HABIT_REASON)));
        String everyDay = DaysOfWeek.toString(
                new boolean[]{true, true, true, true, true, true, false});
        onView(withId(R.id.view_reminder)).check(matches(withText(everyDay)));
        onView(withId(R.id.view_privacy)).check(matches(withText(R.string.isPublic)));
    }

    @Test
    public void D_test_deleteHabit() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.AllHabitsFragment));

        // when you're pissed just double click
        onView(withText(newHabitTitle)).perform(doubleClick());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        onView(withId(R.id.button_delete_habit)).perform(click());
        onView(withText("YES")).perform(click());
        onView(withText(newHabitTitle)).check(doesNotExist());
    }

    private void removeHabit(String uuid) {
        AtomicBoolean isDone = new AtomicBoolean(false);
        sHabitRepo.delete(new FSHabit(uuid, HABIT_TITLE, HABIT_REASON, Instant.now(), DaysOfWeek.emptyArray(), true, 0),
                () -> {
                    Log.e("Delete", "delete successful");
                    isDone.set(true);
                }, e -> {
                    Log.e("Delete", e.getLocalizedMessage());
                    isDone.set(true);
                });

        while (!isDone.get()) {
            SystemClock.sleep(100);
        }

    }

    private void insertHabit(String uuid, int index, String title) {
        AtomicBoolean isDone = new AtomicBoolean(false);
        sHabitRepo.update(uuid, title, HABIT_REASON, Instant.now(),
                DaysOfWeek.emptyArray(), true, index,
                h -> {
                    Log.e("Update", "update successful");
                    isDone.set(true);
                }, e -> {
                    Log.e("Update", e.getLocalizedMessage());
                    isDone.set(true);
                });

        while (!isDone.get()) {
            SystemClock.sleep(100);
        }
    }

    @Before
    public void setup() {
        insertHabit(uuid1, 0, REORDER_TITLE_1);
        insertHabit(uuid2, 1, REORDER_TITLE_2);
    }

    @After
    public void tearDown() {
        removeHabit(uuid1);
        removeHabit(uuid2);
    }

    /**
     * Will not work if the habit with HABIT_TITLE is not in the front
     */
    @Test
    public void E_test_reorderHabit() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.AllHabitsFragment));

        // this is getting really annoying
        SystemClock.sleep(500);
        onView(withId(R.id.reorder_fab)).perform(click());
        SystemClock.sleep(500);

        // aggressively swipe down
        onView(allOf(withId(R.id.lock), withParent(
                nthChildOf(withId(R.id.habit_recycler_view), 0)))).perform(swipeDown());

        onView(withId(R.id.reorder_fab)).perform(click());

        // the habit should now have an index of 1
        onView(withChild(withText(REORDER_TITLE_1))).check(
                matches(withParent(nthChildOf(withId(R.id.habit_recycler_view), 1))));
    }
}
