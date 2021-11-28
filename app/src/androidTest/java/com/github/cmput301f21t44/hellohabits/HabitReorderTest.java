package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.cmput301f21t44.hellohabits.HabitTest.HABIT_REASON;
import static com.github.cmput301f21t44.hellohabits.HabitTest.HABIT_TITLE;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HabitReorderTest {
    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final String REORDER_TITLE_1 = "Reorder Title 1";
    public static final String REORDER_TITLE_2 = "Reorder Title 2";
    public static final String uuid1 = "d500d007-600c-4a06-9a6b-c69ba7638c65";
    public static final String uuid2 = "8d5dd988-7333-42da-bb21-1f3577acc1f0";
    public static HabitRepository sHabitRepo = new FirestoreHabitRepository(sDb, sAuth);

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        TestUtil.initEmulators(sAuth, sDb);
        TestUtil.login(sAuth);

        insertHabit(uuid1, 0, REORDER_TITLE_1);
        insertHabit(uuid2, 1, REORDER_TITLE_2);
    }

    @AfterClass
    public static void tearDown() {
        removeHabit(uuid1);
        removeHabit(uuid2);
    }

    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                view -> GeneralLocation.BOTTOM_CENTER.calculateCoordinates((View) view.getParent().getParent()), Press.FINGER);
    }

    /**
     * Will not work if the habit with HABIT_TITLE is not in the front
     */
    @Test
    public void test_reorderHabit() {
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


    private static void removeHabit(String uuid) {
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

    private static void insertHabit(String uuid, int index, String title) {
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
}
