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

public class FirestoreHabitRepository extends FirestoreRepository implements HabitRepository {
    public FirestoreHabitRepository(FirebaseFirestore db, FirebaseAuth auth) {
        super(db, auth);
    }

    public FirestoreHabitRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());

    }

    @Override
    public LiveData<List<Habit>> getAllHabits() {
        final MediatorLiveData<List<Habit>> habitLiveData = new MediatorLiveData<>();
        getHabitCollectionRef().addSnapshotListener((habitSnapshots, err) -> {
            if (habitSnapshots == null) return;
            List<Habit> habits = new ArrayList<>();
            for (QueryDocumentSnapshot d : habitSnapshots) {
                FSHabit habit = FSHabit.fromSnapshot(d);
                LiveData<List<HabitEvent>> habitEvents = getEventsByHabitId(habit.getId());
                habits.add(habit);
                habitLiveData.addSource(habitEvents, habit::setHabitEvents);
            }
            habitLiveData.setValue(habits);
        });

        return habitLiveData;
    }


    @Override
    public void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                       FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        FSHabit habit = new FSHabit(title, reason, dateStarted, daysOfWeek);
        getHabitCollectionRef().document(habit.getId()).set(FSHabit.getMap(habit))
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }


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

    private void deleteHabit(Habit habit, FirebaseTask.ThenFunction successCallback,
                             FirebaseTask.CatchFunction failCallback) {

        getHabitRef(habit.getId()).delete()
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    @Override
    public void delete(Habit habit, FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {

        getEventCollectionRef(habit.getId()).get()
                .addOnSuccessListener(eventSnapshots ->
                        deleteHabitWithEvents(eventSnapshots, habit.getId())
                                .addOnSuccessListener(u -> successCallback.apply()))
                // only delete the habit (no sub-collections)
                .addOnFailureListener(err -> deleteHabit(habit, successCallback, failCallback));
    }

    @Override
    public void update(String id, String title, String reason, Instant dateStarted,
                       boolean[] daysOfWeek, FirebaseTask.ResultFunction<Habit> successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        FSHabit habit = new FSHabit(id, title, reason, dateStarted, daysOfWeek);
        getHabitRef(habit.getId()).update(FSHabit.getMap(habit))
                .addOnSuccessListener(u -> successCallback.apply(habit))
                .addOnFailureListener(failCallback::apply);
    }
}
