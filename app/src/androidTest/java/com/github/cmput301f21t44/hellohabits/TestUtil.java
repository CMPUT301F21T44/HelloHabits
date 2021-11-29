package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestUtil {
    public static final String ANDROID_LOCALHOST = "10.0.2.2";
    public static int AUTHENTICATION_PORT = 9099;
    public static int FIRE_STORE_PORT = 8080;

    public static void doSynchronous(DoTask task) {
        AtomicBoolean done = new AtomicBoolean(false);
        task.doTask(() -> done.set(true), e -> done.set(true));
        while (!done.get()) {
            SystemClock.sleep(100);
        }
    }

    public static <T> void doSynchronousWithResult(DoTaskWithResult<T> task) {
        AtomicBoolean done = new AtomicBoolean(false);
        task.doTask((u) -> done.set(true), e -> done.set(true));
        while (!done.get()) {
            SystemClock.sleep(100);
        }
    }

    public static void initEmulators(FirebaseAuth auth, FirebaseFirestore db) {
        auth.useEmulator(ANDROID_LOCALHOST, AUTHENTICATION_PORT);
        db.useEmulator(ANDROID_LOCALHOST, FIRE_STORE_PORT);
    }

    public static void login(FirebaseAuth auth) {
        doSynchronous((u, e) -> auth
                .signInWithEmailAndPassword(LoginTest.EMAIL, LoginTest.PASSWORD)
                .addOnSuccessListener(h -> u.apply())
                .addOnFailureListener(e::apply));
    }

    public static String getRealTimeString(String base, int maxLen) {
        String output = base + new StringBuilder(Long.toString(SystemClock.elapsedRealtime()))
                .reverse();
        return maxLen > output.length() ? output : output.substring(0, maxLen);
    }

    interface DoTask {
        void doTask(ThenFunction successCallback, CatchFunction failCallback);

    }

    interface DoTaskWithResult<T> {
        void doTask(ResultFunction<T> resultCallback, CatchFunction failCallback);
    }

    public static void navigateFromSidebar(int resId) {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(resId));
    }
}
