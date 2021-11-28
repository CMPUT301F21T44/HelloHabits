package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.cmput301f21t44.hellohabits.HabitReorderTest.insertHabit;
import static com.github.cmput301f21t44.hellohabits.HabitTest.HABIT_TITLE;

import android.os.SystemClock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewUserHabitsTest extends SocialTest {
    @BeforeClass
    public static void setup() {
        login();
        createOtherUser();
        TestUtil.doSynchronous((u, e) -> sUserRepo.followUser(OTHER_EMAIL, u, e));
        insertHabit(OTHER_EMAIL);
    }

    @Test
    public void test_viewUserHabits() {
        TestUtil.navigateFromSidebar(R.id.FollowingFragment);

        onView(withText(OTHER_EMAIL)).perform(doubleClick());
        SystemClock.sleep(1000);
        onView(withText(HABIT_TITLE)).check(matches(isDisplayed()));
    }
}
