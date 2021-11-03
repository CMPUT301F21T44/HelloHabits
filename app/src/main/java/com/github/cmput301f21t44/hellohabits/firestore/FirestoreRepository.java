package com.github.cmput301f21t44.hellohabits.firestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;
import com.github.cmput301f21t44.hellohabits.model.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirestoreRepository implements HabitRepository, HabitEventRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private FirebaseUser user;

    public FirestoreRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    public FirestoreRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    private FirebaseUser getUser() {
        if (user == null) {
            user = auth.getCurrentUser();
        }
        return user;
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

    @NonNull
    private String getEmail() {
        return Objects.requireNonNull(getUser().getEmail());
    }

    private CollectionReference getHabitCollectionRef() {
        return db.collection(User.COLLECTION).document(getEmail()).collection(FSHabit.COLLECTION);
    }

    private DocumentReference getHabitRef(String habitId) {
        assert !habitId.equals("");
        return getHabitCollectionRef().document(habitId);
    }

    private CollectionReference getEventCollectionRef(String habitId) {
        return getHabitRef(habitId).collection(FSHabitEvent.COLLECTION);
    }

    private DocumentReference getEventRef(String eventId, String habitId) {
        return getEventCollectionRef(habitId).document(eventId);
    }

    @Override
    public void delete(Habit habit) {
        getEventCollectionRef(habit.getId()).get().addOnSuccessListener(g -> {
            // delete event collections in a batch
            WriteBatch batch = db.batch();
            for (DocumentSnapshot d : g.getDocuments()) {
                batch.delete(d.getReference());
            }
            // delete habit ref
            batch.delete(getHabitRef(habit.getId()));
            batch.commit();
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


    @Override
    public void insert(String habitId, String comment) {
        final FSHabitEvent event = new FSHabitEvent(Instant.now(), habitId, comment);
        getEventRef(event.getId(), habitId).set(FSHabitEvent.getMap(event));
    }

    @Override
    public void delete(HabitEvent habitEvent) {
        getEventRef(habitEvent.getId(), habitEvent.getHabitId()).delete();
    }

    @Override
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

    @Override
    public HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        FSHabitEvent event = new FSHabitEvent(id, date, habitId, comment, photoPath, location);
        getEventRef(id, habitId).update(FSHabitEvent.getMap(event));
        return event;
    }
}
