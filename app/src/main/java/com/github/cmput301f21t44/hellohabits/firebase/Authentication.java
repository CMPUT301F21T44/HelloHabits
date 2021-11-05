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

    public Authentication(FirebaseAuth auth, FirebaseFirestore db) {
        this.mAuth = auth;
        this.mDb = db;
    }

    public Authentication() {
        this(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
    }

    public void signup(String name, String email, String password,
                       FirebaseTask.ThenFunction successCallback, FirebaseTask.CatchFunction failCallback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            mDb.collection("users").document(email).set(user)
                    .addOnSuccessListener(u -> successCallback.apply());
        }).addOnFailureListener(failCallback::apply);
    }

    public Task<AuthResult> signIn(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signOut() {
        mAuth.signOut();
    }
}
