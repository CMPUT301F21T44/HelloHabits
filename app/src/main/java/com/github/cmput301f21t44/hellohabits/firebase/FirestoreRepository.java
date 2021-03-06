package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract FirestoreRepository for general methods
 */
public abstract class FirestoreRepository {
    protected final FirebaseFirestore mDb;
    protected final FirebaseAuth mAuth;

    /**
     * Map for storing LiveData of Habit events
     */
    protected final Map<String, LiveData<List<HabitEvent>>> habitEventLiveDataMap = new HashMap<>();

    /**
     * Creates a new FirestoreRepository
     *
     * @param db   FirebaseFirestore instance
     * @param auth FirebaseAuth instance
     */
    protected FirestoreRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.mDb = db;
        this.mAuth = auth;
    }

    /**
     * Get current user
     *
     * @return The current FireBaseUser
     */
    protected FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Get the current user's email
     *
     * @return String representing the current user's email
     */
    @NonNull
    protected String getEmail() {
        return Objects.requireNonNull(getUser().getEmail());
    }

    /**
     * Get a CollectionReference for a user's Habits
     *
     * @param email The user's email
     * @return CollectionReference to a user's Habits
     */
    protected CollectionReference getHabitCollectionRef(String email) {
        return getUserSubCollectionRef(email, FSHabit.COLLECTION);
    }

    /**
     * Get a CollectionReference for the current user's Habits
     *
     * @return CollectionReference to the current user's Habits
     */
    protected CollectionReference getHabitCollectionRef() {
        return getHabitCollectionRef(getEmail());
    }

    /**
     * Gets a CollectionReference to all users
     *
     * @return CollectionReference to all users
     */
    protected CollectionReference getAllUsersCollectionRef() {
        return mDb.collection(FSUser.COLLECTION);
    }

    /**
     * Get a DocumentReference to a User
     *
     * @param email The user's email
     * @return DocumentReference to the user
     */
    protected DocumentReference getUserRef(String email) {
        return getAllUsersCollectionRef().document(email);
    }

    /**
     * Get a sub-collection from a user document
     *
     * @param email      The User's email
     * @param collection Name of collection
     * @return CollectionReference with the given collection name
     */
    protected CollectionReference getUserSubCollectionRef(String email, String collection) {
        return getUserRef(email).collection(collection);
    }

    /**
     * Get a DocumentReference for a Habit
     *
     * @param habitId UUID of the Habit
     * @param email   The User's email
     * @return DocumentReference to the Habit
     */
    protected DocumentReference getHabitRef(String habitId, String email) {
        return getHabitCollectionRef(email).document(habitId);
    }

    /**
     * Get a DocumentReference for a Habit
     *
     * @param habitId UUID of the Habit
     * @return DocumentReference to the Habit
     */
    protected DocumentReference getHabitRef(String habitId) {
        return getHabitCollectionRef().document(habitId);
    }


    /**
     * Get a CollectionReference for a Habit's HabitEvents
     *
     * @param habitId UUID of the Habit
     * @param email   The User's email
     * @return CollectionReference to the Habit's HabitEvents
     */
    protected CollectionReference getEventCollectionRef(String habitId, String email) {
        return getHabitRef(habitId, email).collection(FSHabitEvent.COLLECTION);
    }

    /**
     * Get a CollectionReference for a Habit's HabitEvents
     *
     * @param habitId UUID of the Habit
     * @return CollectionReference to the Habit's HabitEvents
     */
    protected CollectionReference getEventCollectionRef(String habitId) {
        return getHabitRef(habitId).collection(FSHabitEvent.COLLECTION);
    }

    /**
     * Create a LiveData List of HabitEvents from a Habit with the given UUID
     *
     * @param habitId UUID of the Habit
     * @param email   The User's email
     * @return a LiveData List of HabitEvents
     */
    private LiveData<List<HabitEvent>> newHabitEventLiveData(String habitId, String email) {
        final MediatorLiveData<List<HabitEvent>> eventLiveData = new MediatorLiveData<>();
        getEventCollectionRef(habitId, email).addSnapshotListener((eventSnapshots, err) -> {
            if (eventSnapshots == null) return;
            final List<HabitEvent> habitEvents = new ArrayList<>();
            for (QueryDocumentSnapshot doc : eventSnapshots) {
                habitEvents.add(new FSHabitEvent(doc));
            }
            eventLiveData.setValue(habitEvents);
        });
        return eventLiveData;
    }

    /**
     * Gets a LiveData list of HabitEvents from the cache map
     * <p>
     * Creates a new one if it doesn't exist
     *
     * @param habitId UUID of the Habit
     * @param email   The User's email
     * @return a LiveData List of HabitEvents
     */
    public LiveData<List<HabitEvent>> getEventsByHabitId(String habitId, String email) {
        String mapKey = String.format("%s/%s", email, habitId);
        LiveData<List<HabitEvent>> habitEventLiveData = habitEventLiveDataMap.get(mapKey);
        if (habitEventLiveData != null) return habitEventLiveData;

        habitEventLiveData = newHabitEventLiveData(habitId, email);
        habitEventLiveDataMap.put(mapKey, habitEventLiveData);
        return habitEventLiveData;
    }

    /**
     * Create a LiveData List of HabitEvents from a Habit with the given UUID
     *
     * @param habitId UUID of the Habit
     * @return a LiveData List of HabitEvents
     */
    public LiveData<List<HabitEvent>> getEventsByHabitId(String habitId) {
        return getEventsByHabitId(habitId, getEmail());
    }
}
