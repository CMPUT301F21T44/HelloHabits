package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FollowTest extends SocialTest {
    @BeforeClass
    public static void setup() {
        login();
        createOtherUser();
    }

    @AfterClass
    public static void tearDown() {
        removeFollows();
    }

    @Test
    public void test_follow() {
        TestUtil.navigateFromSidebar(R.id.AllUsersFragment);

        // the users take a while
        SystemClock.sleep(2000);

        getUserListButton(R.id.reject).perform(click());
        SystemClock.sleep(500);
        getUserListButton(R.id.reject).check(matches(withText(R.string.requested)));
    }
}
