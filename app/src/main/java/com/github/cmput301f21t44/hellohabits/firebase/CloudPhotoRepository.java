package com.github.cmput301f21t44.hellohabits.firebase;

import android.net.Uri;

import com.github.cmput301f21t44.hellohabits.model.habitevent.PhotoRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CloudPhotoRepository implements PhotoRepository {
    private final StorageReference mRootRef;

    public CloudPhotoRepository(FirebaseStorage storage) {
        this.mRootRef = storage.getReference();
    }

    @Override
    public void uploadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback) {
        mRootRef.child(file.getLastPathSegment()).putFile(file)
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    @Override
    public void downloadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback) {
        mRootRef.child(file.getLastPathSegment()).getFile(file)
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    @Override
    public void deletePhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback) {
        mRootRef.child(file.getLastPathSegment()).delete()
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }
}
