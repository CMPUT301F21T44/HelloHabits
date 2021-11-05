package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;

/**
 * Firestore Repository for HabitEvents
 */
public class FirestoreEventRepository extends FirestoreRepository implements HabitEventRepository {
    /**
     * Creates a new FirestoreEventRepository
     *
     * @param db   FirebaseFirestore instance
     * @param auth FirebaseAuth instance
     */
    public FirestoreEventRepository(FirebaseFirestore db, FirebaseAuth auth) {
        super(db, auth);
    }

    /**
     * Creates a new FirestoreEventRepository using getInstance for FirebaseAuth and FirebaseFirestore
     */
    public FirestoreEventRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Insert a new HabitEvent
     *
     * @param habitId         UUID of the Habit parent
     * @param comment         Optional comment
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void insert(String habitId, String comment, FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        final FSHabitEvent event = new FSHabitEvent(Instant.now(), habitId, comment);
        getEventRef(event.getId(), habitId).set(FSHabitEvent.getMap(event))
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    /**
     * Delete a given HabitEvent
     *
     * @param habitEvent   HabitEvent to delete
     * @param failCallback Callback for when the operation fails
     */
    @Override
    public void delete(HabitEvent habitEvent, FirebaseTask.CatchFunction failCallback) {
        getEventRef(habitEvent.getId(), habitEvent.getHabitId()).delete()
                .addOnFailureListener(failCallback::apply);
    }

    /**
     * Update a HabitEvent with the given UUID
     *
     * @param id              UUID of the HabitEvent
     * @param habitId         UUID of the Habit parent
     * @param date            Instant of when the HabitEvent was denoted
     * @param comment         Optional comment
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void update(String id, String habitId, Instant date, String comment,
                       FirebaseTask.ResultFunction<HabitEvent> successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        FSHabitEvent event = new FSHabitEvent(id, date, habitId, comment);
        getEventRef(id, habitId).update(FSHabitEvent.getMap(event))
                .addOnSuccessListener(u -> successCallback.apply(event))
                .addOnFailureListener(failCallback::apply);
    }
}
