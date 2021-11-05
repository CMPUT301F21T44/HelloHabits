package com.github.cmput301f21t44.hellohabits.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Authentication {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mDb;

    /**
     * This function get the auth and database down to this class
     *
     * @param auth the auth
     * @param db   the database in fire store
     */
    public Authentication(FirebaseAuth auth, FirebaseFirestore db) {
        this.mAuth = auth;
        this.mDb = db;
    }

    /**
     * This function gets the instance of auth and database
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
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            mDb.collection("users").document(email).set(user)
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

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
