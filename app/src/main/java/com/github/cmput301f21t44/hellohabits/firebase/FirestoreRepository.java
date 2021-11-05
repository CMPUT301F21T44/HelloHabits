package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract FirestoreRepository for general methods
 */
public abstract class FirestoreRepository {
    protected final FirebaseFirestore mDb;
    protected final FirebaseAuth mAuth;
    protected FirebaseUser mUser;

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
     * Generate an instant object from a DocumentSnapshot field
     * @param doc DocumentSnapshot from which to get the Instant
     * @param field Key of the Instant field
     * @return Instant from the DocumentSnapshot
     */
    public static Instant instantFromDoc(DocumentSnapshot doc, String field) {
        Long epochSeconds = doc.getLong(field + ".epochSecond");
        Long nanoAdjustment = doc.getLong(field + ".nano");
        return (epochSeconds != null && nanoAdjustment != null) ?
                Instant.ofEpochSecond(epochSeconds, nanoAdjustment) : null;
    }

    /**
     * Get current user
     * @return The current FireBaseUser
     */
    protected FirebaseUser getUser() {
        if (mUser == null) {
            mUser = mAuth.getCurrentUser();
        }
        return mUser;
    }

    /**
     * Get the current user's email
     * @return String representing the current user's email
     */
    @NonNull
    protected String getEmail() {
        return Objects.requireNonNull(getUser().getEmail());
    }

    /**
     * Get a DocumentReference for a HabitEvent
     * @param eventId UUID of the HabitEvent
     * @param habitId UUID of the parent Habit
     * @return DocumentReference to the HabitEvent
     */
    protected DocumentReference getEventRef(String eventId, String habitId) {
        return getEventCollectionRef(habitId).document(eventId);
    }

    /**
     * Get a CollectionReference for a user's Habits
     * @return CollectionReference to a user's Habits
     */
    protected CollectionReference getHabitCollectionRef() {
        return mDb.collection(User.COLLECTION).document(getEmail()).collection(FSHabit.COLLECTION);
    }

    /**
     * Get a DocumentReference for a Habit
     * @param habitId UUID of the Habit
     * @return DocumentReference to the Habit
     */
    protected DocumentReference getHabitRef(String habitId) {
        return getHabitCollectionRef().document(habitId);
    }

    /**
     * Get a CollectionReference for a Habit's HabitEvents
     * @param habitId UUID of the Habit
     * @return CollectionReference to the Habit's HabitEvents
     */
    protected CollectionReference getEventCollectionRef(String habitId) {
        return getHabitRef(habitId).collection(FSHabitEvent.COLLECTION);
    }

    /**
     * Create a LiveData List of HabitEvents from a Habit with the given UUID
     * @param habitId UUID of the Habit
     * @return a LiveData List of HabitEvents
     */
    public LiveData<List<HabitEvent>> getEventsByHabitId(String habitId) {
        final MutableLiveData<List<HabitEvent>> eventLiveData = new MutableLiveData<>();
        getEventCollectionRef(habitId).addSnapshotListener((eventSnapshots, err) -> {
            if (eventSnapshots == null) return;
            final List<HabitEvent> habitEvents = new ArrayList<>();
            for (QueryDocumentSnapshot doc : eventSnapshots) {
                habitEvents.add(FSHabitEvent.fromSnapshot(doc));
            }
            eventLiveData.setValue(habitEvents);
        });
        return eventLiveData;
    }
}
