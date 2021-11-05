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
    public void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        FSHabit habit = new FSHabit(title, reason, dateStarted, daysOfWeek);
        getHabitCollectionRef().document(habit.getId()).set(FSHabit.getMap(habit));
    }


    private Task<Void> deleteHabitWithEvents(QuerySnapshot eventSnapshots, String habitId) {
        // delete event collections in a batch
        WriteBatch batch = db.batch();
        for (DocumentSnapshot d : eventSnapshots.getDocuments()) {
            batch.delete(d.getReference());
        }
        // delete habit ref
        batch.delete(getHabitRef(habitId));
        return batch.commit();
    }

    @Override
    public void delete(Habit habit) {
        getEventCollectionRef(habit.getId()).get()
                .addOnSuccessListener(eventSnapshots ->
                        deleteHabitWithEvents(eventSnapshots, habit.getId())
                                .addOnSuccessListener(u -> {


                                })).addOnFailureListener(err -> {
            // only delete
            getHabitRef(habit.getId()).delete().addOnSuccessListener(u -> {

            }).addOnFailureListener(e -> {
            });

        });

    }

    @Override
    public Habit update(String id, String title, String reason, Instant dateStarted,
                        boolean[] daysOfWeek) {
        FSHabit habit = new FSHabit(id, title, reason, dateStarted, daysOfWeek);
        getHabitRef(habit.getId()).update(FSHabit.getMap(habit))
                .addOnSuccessListener(u -> {
                }).addOnFailureListener(e -> {
        });

        return habit;
    }
}
