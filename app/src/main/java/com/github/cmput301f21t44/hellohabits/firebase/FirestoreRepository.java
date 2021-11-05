package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FirestoreRepository {
    protected final FirebaseFirestore db;
    protected final FirebaseAuth auth;
    protected FirebaseUser user;

    protected FirestoreRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    protected FirebaseUser getUser() {
        if (user == null) {
            user = auth.getCurrentUser();
        }
        return user;
    }

    @NonNull
    protected String getEmail() {
        return Objects.requireNonNull(getUser().getEmail());
    }

    protected DocumentReference getEventRef(String eventId, String habitId) {
        return getEventCollectionRef(habitId).document(eventId);
    }

    protected CollectionReference getHabitCollectionRef() {
        return db.collection(User.COLLECTION).document(getEmail()).collection(FSHabit.COLLECTION);
    }

    protected DocumentReference getHabitRef(String habitId) {
        return getHabitCollectionRef().document(habitId);
    }

    protected CollectionReference getEventCollectionRef(String habitId) {
        return getHabitRef(habitId).collection(FSHabitEvent.COLLECTION);
    }

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
