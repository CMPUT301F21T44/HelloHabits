package com.github.cmput301f21t44.hellohabits;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.github.cmput301f21t44.hellohabits.firebase.FSDocument;
import com.github.cmput301f21t44.hellohabits.firebase.FSUser;
import com.github.cmput301f21t44.hellohabits.firebase.FirestoreUserRepository;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;

public class SocialTest {
    public static String OTHER_NAME = "Other TestUser";
    public static String OTHER_EMAIL = "testUser2@example.com";
    public static FirebaseAuth sAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static FirestoreUserRepository sUserRepo = new FirestoreUserRepository(sDb, sAuth);

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    public static void login() {
        TestUtil.initEmulators(sAuth, sDb);
        TestUtil.login(sAuth);
    }

    public static void createOtherUser() {
        TestUtil.doSynchronous((u, e) -> FSDocument.set(new FSUser(OTHER_EMAIL, OTHER_NAME),
                e, sDb.collection(FSUser.COLLECTION))
                .addOnSuccessListener(h -> u.apply()));
    }

    public static void removeFollows() {
        // this also removes follows
        TestUtil.doSynchronous((u, e) -> sUserRepo.cancelFollow(OTHER_EMAIL, u, e));
        TestUtil.doSynchronous((u, e) -> sUserRepo.rejectFollow(OTHER_EMAIL, u, e));
    }

    public static ViewInteraction getUserListButton(int buttonResId) {
        return onView(allOf(withId(buttonResId), hasSibling(withChild(withText(OTHER_EMAIL)))));
    }
}
