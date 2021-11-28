package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FollowerTest extends SocialTest {
    @BeforeClass
    public static void setup() {
        login();
        createOtherUser();
    }

    @AfterClass
    public static void tearDown() {
        removeFollows();
    }

    @Before
    public void setupFollows() {
        // so that we don't have to worry about buttons
        TestUtil.doSynchronous((u, e) -> sUserRepo.followUser(OTHER_EMAIL, u, e));
        TestUtil.doSynchronous((u, e) -> sUserRepo.getFollowRequest(OTHER_EMAIL, u, e));
    }

    @Test
    public void test_acceptFollow() {
        TestUtil.navigateFromSidebar(R.id.FollowersFragment);

        getUserListButton(R.id.accept).perform(doubleClick());
        SystemClock.sleep(1000);
        getUserListButton(R.id.accept)
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void test_rejectFollow() {
        TestUtil.navigateFromSidebar(R.id.FollowersFragment);

        getUserListButton(R.id.reject).perform(doubleClick());
        SystemClock.sleep(1000);
        onView(withText(OTHER_EMAIL)).check(doesNotExist());
    }
}
