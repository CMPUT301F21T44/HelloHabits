package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.Location;
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
    public void insert(String habitId, String comment) {
        final FSHabitEvent event = new FSHabitEvent(Instant.now(), habitId, comment);
        getEventRef(event.getId(), habitId).set(FSHabitEvent.getMap(event));
    }

    @Override
    public void delete(HabitEvent habitEvent) {
        getEventRef(habitEvent.getId(), habitEvent.getHabitId()).delete();
    }

    @Override
    public HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        FSHabitEvent event = new FSHabitEvent(id, date, habitId, comment, photoPath, location);
        getEventRef(id, habitId).update(FSHabitEvent.getMap(event));
        return event;
    }
}
