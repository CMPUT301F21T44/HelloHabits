package com.github.cmput301f21t44.hellohabits.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles authentication with Firebase
 */
public class Authentication {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mDb;

    /**
     * Creates a new Authentication object
     *
     * @param auth FirebaseAuth instance
     * @param db   FirebaseFirestore instance
     */
    public Authentication(FirebaseAuth auth, FirebaseFirestore db) {
        this.mAuth = auth;
        this.mDb = db;
    }

    /**
     * Creates a new Authentication object using getInstance for FirebaseAuth and FirebaseFirestore
     */
    public Authentication() {
        this(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
    }

    /**
     * This function handle the users' sign-up event with name, email and password
     *
     * @param name     a string of user name
     * @param email    a string of user email address
     * @param password a string of password
     */
    public void signup(String name, String email, String password,
                       FirebaseTask.ThenFunction successCallback, FirebaseTask.CatchFunction failCallback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            FSUser user = new FSUser(name, email);
            mDb.collection(FSUser.COLLECTION).document(email).set(FSUser.getMap(user))
                    .addOnSuccessListener(u -> successCallback.apply());
        }).addOnFailureListener(failCallback::apply);
    }

    /**
     * This function returns a successful sign-in
     *
     * @param email    a string of user email address
     * @param password a string of password
     * @return sign-in with a valid email and password
     */
    public Task<AuthResult> signIn(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * This function allow user to sign out
     */
    public void signOut() {
        mAuth.signOut();
    }

    /**
     * Gets the current signed in user
     *
     * @return the current user, null if not signed in
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
