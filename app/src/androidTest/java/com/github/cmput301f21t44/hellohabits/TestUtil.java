package com.github.cmput301f21t44.hellohabits;

import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestUtil {
    public static final String ANDROID_LOCALHOST = "10.0.2.2";
    public static int AUTHENTICATION_PORT = 9099;
    public static int FIRESTORE_PORT = 8080;

    public static void initEmulators(FirebaseAuth auth, FirebaseFirestore db) {
        auth.useEmulator(ANDROID_LOCALHOST, AUTHENTICATION_PORT);
        db.useEmulator(ANDROID_LOCALHOST, FIRESTORE_PORT);
    }
    public static void login(FirebaseAuth auth) {
        AtomicBoolean isSignedIn = new AtomicBoolean(false);
        auth.signInWithEmailAndPassword(LoginTest.EMAIL, LoginTest.PASSWORD)
                .addOnSuccessListener(u-> isSignedIn.set(true));

        while (!isSignedIn.get()) {
            SystemClock.sleep(100);
        }

    }
}
