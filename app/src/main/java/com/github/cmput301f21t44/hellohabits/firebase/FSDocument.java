package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;


public interface FSDocument<T extends FSDocument<T>> {
    @NonNull
    static <T extends FSDocument<T>> T create(QueryDocumentSnapshot doc, Class<T> docClass) {
        if (docClass.isAssignableFrom(FSUser.class)) {
            return (T) new FSUser(doc);
        } else if (docClass.isAssignableFrom(FSFollow.class)) {
            return (T) new FSFollow(doc);
        } else if (docClass.isAssignableFrom(FSHabit.class)) {
            return (T) new FSHabit(doc);
        } else if (docClass.isAssignableFrom(FSHabitEvent.class)) {
            return (T) new FSHabitEvent(doc);
        }
        throw new IllegalArgumentException("Unknown FSDocument class: " + docClass.getName());
    }

    static <T extends FSDocument<T>> Task<Void> set(T doc, CatchFunction failCallback,
                                                    CollectionReference collectionRef) {
        return collectionRef.document(doc.getKey()).set(doc.getMap(), SetOptions.merge())
                .addOnFailureListener(failCallback::apply);
    }

    static <T extends FSDocument<T>> Task<Void> delete(T doc, CatchFunction failCallback,
                                                       CollectionReference collectionRef) {
        return collectionRef.document(doc.getKey()).delete()
                .addOnFailureListener(failCallback::apply);
    }

    Map<String, Object> getMap();

    String getKey();
}
