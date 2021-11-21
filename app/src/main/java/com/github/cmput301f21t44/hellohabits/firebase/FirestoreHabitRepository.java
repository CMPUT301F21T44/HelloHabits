package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Firestore Repository for Habits
 */
public class FirestoreHabitRepository extends FirestoreRepository implements HabitRepository {
    /**
     * Creates a new FirestoreHabitRepository
     *
     * @param db   FirebaseFirestore instance
     * @param auth FirebaseAuth instance
     */
    public FirestoreHabitRepository(FirebaseFirestore db, FirebaseAuth auth) {
        super(db, auth);
    }

    /**
     * Creates a new FirestoreHabitRepository using getInstance for FirebaseAuth and FirebaseFirestore
     */
    public FirestoreHabitRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Return a list of all Habits of the current user
     *
     * @return List of all Habits
     */
    @Override
    public LiveData<List<Habit>> getAllHabits() {
        return getUserHabits(getEmail(), true);
    }

    private LiveData<List<Habit>> getUserHabits(String email, boolean includePrivate) {
        final MediatorLiveData<List<Habit>> habitLiveData = new MediatorLiveData<>();
        getHabitCollectionRef(email).addSnapshotListener((habitSnapshots, err) -> {
            if (habitSnapshots == null) return;
            List<Habit> habits = new ArrayList<>();
            for (QueryDocumentSnapshot d : habitSnapshots) {
                FSHabit habit = new FSHabit(d);
                LiveData<List<HabitEvent>> habitEvents = getEventsByHabitId(habit.getId(), email);
                if (includePrivate || !habit.isPrivate()) {
                    habits.add(habit);
                    habitLiveData.addSource(habitEvents, habit::setHabitEvents);
                }
            }
            habitLiveData.setValue(habits);
        });

        return habitLiveData;
    }

    @Override
    public LiveData<List<Habit>> getUserHabits(String email) {
        return getUserHabits(email, false);
    }


    /**
     * Creates a new Habit for the user
     *
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param isPrivate       Whether the habit is invisible to followers
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                       boolean isPrivate, ThenFunction successCallback,
                       CatchFunction failCallback) {
        FSHabit habit = new FSHabit(title, reason, dateStarted, daysOfWeek, isPrivate);
        FSDocument.set(habit, failCallback, getHabitCollectionRef())
                .addOnSuccessListener(u -> successCallback.apply());
    }


    /**
     * Deletes a Habit containing events in a single transaction
     *
     * @param eventSnapshots Snapshots of HabitEvents
     * @param habitId        UUID of the Habit to delete
     * @return Task to which to add callbacks
     */
    private Task<Void> deleteHabitWithEvents(QuerySnapshot eventSnapshots, String habitId) {
        // delete event collections in a batch
        WriteBatch batch = mDb.batch();
        for (DocumentSnapshot d : eventSnapshots.getDocuments()) {
            batch.delete(d.getReference());
        }
        // delete habit ref
        batch.delete(getHabitRef(habitId));
        return batch.commit();
    }

    /**
     * Delete a habit with no events
     *
     * @param habit           Habit to delete
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    private void deleteHabit(Habit habit, ThenFunction successCallback,
                             CatchFunction failCallback) {
        FSDocument.delete(new FSHabit(habit), failCallback, getHabitCollectionRef())
                .addOnSuccessListener(u -> successCallback.apply());
    }

    /**
     * Delete a given Habit, along with any HabitEvents under it
     *
     * @param habit           Habit to delete
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void delete(Habit habit, ThenFunction successCallback,
                       CatchFunction failCallback) {
        getEventCollectionRef(habit.getId()).get()
                .addOnSuccessListener(eventSnapshots ->
                        deleteHabitWithEvents(eventSnapshots, habit.getId())
                                .addOnSuccessListener(u -> successCallback.apply())
                                .addOnFailureListener(failCallback::apply)
                )
                // only delete the habit (no sub-collections)
                .addOnFailureListener(err -> deleteHabit(habit, successCallback, failCallback));
    }

    /**
     * Update a Habit with the given UUID
     *
     * @param id              UUID of the Habit
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void update(String id, String title, String reason, Instant dateStarted,
                       boolean[] daysOfWeek, boolean isPrivate, ResultFunction<Habit> successCallback,
                       CatchFunction failCallback) {
        FSHabit habit = new FSHabit(id, title, reason, dateStarted, daysOfWeek, isPrivate);
        FSDocument.set(habit, failCallback, getHabitCollectionRef())
                .addOnSuccessListener(u -> successCallback.apply(habit));
    }
}
