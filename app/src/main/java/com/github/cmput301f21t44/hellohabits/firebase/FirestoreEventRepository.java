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
    public void insert(String habitId, String comment, ThenFunction successCallback,
                       CatchFunction failCallback) {
        FSDocument.set(new FSHabitEvent(Instant.now(), habitId, comment), failCallback,
                getEventCollectionRef(habitId))
                .addOnSuccessListener(u -> successCallback.apply());
    }

    /**
     * Delete a given HabitEvent
     *
     * @param habitEvent   HabitEvent to delete
     * @param failCallback Callback for when the operation fails
     */
    @Override
    public void delete(HabitEvent habitEvent, CatchFunction failCallback) {
        FSDocument.delete(new FSHabitEvent((habitEvent)), failCallback,
                getEventCollectionRef(habitEvent.getHabitId()));
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
                       ResultFunction<HabitEvent> successCallback,
                       CatchFunction failCallback) {
        FSHabitEvent event = new FSHabitEvent(id, date, habitId, comment);
        FSDocument.set(event, failCallback, getEventCollectionRef(habitId))
                .addOnSuccessListener(u -> successCallback.apply(event));
    }
}
