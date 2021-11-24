package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HasKey;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.Instant;
import java.util.Map;


public interface FSDocument extends HasKey {
    static <T extends FSDocument> Task<Void> set(T doc, CatchFunction failCallback,
                                                 CollectionReference collectionRef) {
        return collectionRef.document(doc.getKey()).set(doc.getMap(), SetOptions.merge())
                .addOnFailureListener(failCallback::apply);
    }

    static <T extends FSDocument> Task<Void> delete(T doc, CatchFunction failCallback,
                                                    CollectionReference collectionRef) {
        return collectionRef.document(doc.getKey()).delete()
                .addOnFailureListener(failCallback::apply);
    }

    /**
     * Generate an instant object from a DocumentSnapshot field
     *
     * @param doc   DocumentSnapshot from which to get the Instant
     * @param field Key of the Instant field
     * @return Instant from the DocumentSnapshot
     */
    static Instant instantFromDoc(DocumentSnapshot doc, String field) {
        Long epochSeconds = doc.getLong(field + ".epochSecond");
        Long nanoAdjustment = doc.getLong(field + ".nano");
        return (epochSeconds != null && nanoAdjustment != null) ?
                Instant.ofEpochSecond(epochSeconds, nanoAdjustment) : null;
    }

    Map<String, Object> getMap();
}
