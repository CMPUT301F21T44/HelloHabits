package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;

public class FirestoreEventRepository extends FirestoreRepository implements HabitEventRepository {
    public FirestoreEventRepository(FirebaseFirestore db, FirebaseAuth auth) {
        super(db, auth);
    }

    public FirestoreEventRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    @Override
    public void insert(String habitId, String comment, FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        final FSHabitEvent event = new FSHabitEvent(Instant.now(), habitId, comment);
        getEventRef(event.getId(), habitId).set(FSHabitEvent.getMap(event))
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    @Override
    public void delete(HabitEvent habitEvent, FirebaseTask.CatchFunction failCallback) {
        getEventRef(habitEvent.getId(), habitEvent.getHabitId()).delete()
                .addOnFailureListener(failCallback::apply);
    }

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
