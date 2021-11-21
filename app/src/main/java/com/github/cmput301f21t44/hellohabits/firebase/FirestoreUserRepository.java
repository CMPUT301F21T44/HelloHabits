package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.model.User;
import com.github.cmput301f21t44.hellohabits.model.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Firestore Repository for Users
 */
public class FirestoreUserRepository extends FirestoreRepository implements UserRepository {

    /**
     * Creates a new FirestoreUserRepository
     *
     * @param db   FirebaseFirestore instance
     * @param auth FirebaseAuth instance
     */
    public FirestoreUserRepository(FirebaseFirestore db, FirebaseAuth auth) {
        super(db, auth);
    }

    /**
     * Creates a new FirestoreUserRepository using getInstance for FirebaseAuth and FirebaseFirestore
     */
    public FirestoreUserRepository() {
        this(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    }

    /**
     * Create a LiveData list of follows
     *
     * @param collection FollowCollection from which to grab follows
     * @return LiveData of list of follows
     */
    private LiveData<List<User.Follow>> getFollows(FSFollow.FollowCollection collection) {
        final MediatorLiveData<List<User.Follow>> followLiveData = new MediatorLiveData<>();
        getUserCollectionRef(getEmail(), collection.getName())
                .addSnapshotListener((followSnapshots, err) -> {
                    if (followSnapshots == null) return;
                    List<User.Follow> follows = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : followSnapshots) {
                        follows.add(new FSFollow(snapshot));
                    }
                    followLiveData.setValue(follows);
                });

        return followLiveData;
    }

    /**
     * Get list of users that follow the current user
     *
     * @return List of followers
     */
    @Override
    public LiveData<List<User.Follow>> getAllFollowers() {
        return getFollows(FSFollow.FOLLOWER_COLLECTION);
    }

    /**
     * Get list of users that the current user follows
     *
     * @return List of users being followed by the current user
     */
    @Override
    public LiveData<List<User.Follow>> getAllFollowing() {
        return getFollows(FSFollow.FOLLOWING_COLLECTION);
    }

    /**
     * Updates the status of following and followers of two users
     * <p>
     * If the status is REJECTED, deletes the entry from both users
     *
     * @param follower        The email of the following user
     * @param following       The email of the user to be followed
     * @param status          Status of the follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    private void updateFollow(String follower, String following, User.Follow.Status status,
                              ThenFunction successCallback,
                              CatchFunction failCallback) {
        WriteBatch batch = mDb.batch();
        DocumentReference followerRef = getUserCollectionRef(follower,
                FSFollow.FOLLOWING_COLLECTION.getName()).document(following);
        DocumentReference followingRef = getUserCollectionRef(following,
                FSFollow.FOLLOWER_COLLECTION.getName()).document(follower);
        if (status != User.Follow.Status.REJECTED) {
            // will either create or update
            batch.set(followerRef, new FSFollow(following, status), SetOptions.merge());
            batch.set(followingRef, new FSFollow(follower, status), SetOptions.merge());
        } else {
            // delete rejected follow
            batch.delete(followerRef);
            batch.delete(followingRef);
        }
        batch.commit()
                .addOnSuccessListener(u -> successCallback.apply())
                .addOnFailureListener(failCallback::apply);
    }

    /**
     * Requests to follow a user
     *
     * @param email           The email of the user to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void requestFollow(String email, ThenFunction successCallback, CatchFunction failCallback) {
        updateFollow(getEmail(), email, User.Follow.Status.REQUESTED, successCallback, failCallback);
    }

    /**
     * Cancels a follow request
     *
     * @param email           The email of the user to cancel following
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void cancelFollow(String email, ThenFunction successCallback, CatchFunction failCallback) {
        updateFollow(getEmail(), email, User.Follow.Status.REJECTED, successCallback, failCallback);

    }

    /**
     * Accepts a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void acceptFollow(String email, ThenFunction successCallback, CatchFunction failCallback) {
        updateFollow(email, getEmail(), User.Follow.Status.ACCEPTED, successCallback, failCallback);
    }

    /**
     * Rejects a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    @Override
    public void rejectFollow(String email, ThenFunction successCallback, CatchFunction failCallback) {
        updateFollow(email, getEmail(), User.Follow.Status.REJECTED, successCallback, failCallback);
    }
}
