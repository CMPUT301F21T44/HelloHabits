package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;
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
    private LiveData<List<Follow>> getFollows(FSFollow.FollowCollection collection) {
        final MediatorLiveData<List<Follow>> followLiveData = new MediatorLiveData<>();
        getUserSubCollectionRef(getEmail(), collection.getName())
                .addSnapshotListener((followSnapshots, err) -> {
                    if (followSnapshots == null) return;
                    List<Follow> follows = new ArrayList<>();
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
    public LiveData<List<Follow>> getAllFollowers() {
        return getFollows(FSFollow.FOLLOWER_COLLECTION);
    }

    /**
     * Get list of users that the current user follows
     *
     * @return List of users being followed by the current user
     */
    @Override
    public LiveData<List<Follow>> getAllFollowing() {
        return getFollows(FSFollow.FOLLOWING_COLLECTION);
    }

    @Override
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        getAllUsersCollectionRef().addSnapshotListener((userSnapshots, err) -> {
            if (userSnapshots == null) return;
            final List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot doc : userSnapshots) {
                users.add(new FSUser(doc));
            }
            usersLiveData.setValue(users);
        });
        return usersLiveData;
    }

    /**
     * Get info of a specific user
     *
     * @param email The user's email
     * @return LiveData of User if they are followed,
     * value is null if not followed or the user doesn't exist
     */
    @Override
    public LiveData<User> getUser(String email) {
        MediatorLiveData<User> userLivedata = new MediatorLiveData<>();
        getUserRef(email).addSnapshotListener((doc, err) -> {
            // user not found
            if (doc == null) return;
            userLivedata.addSource(getAllFollowing(), follows -> {
                // will set user to null if user is not followed
                boolean visibleProfile = follows.stream()
                        .anyMatch(d -> d.getEmail().equals(email) &&
                                d.getStatus().equals(Follow.Status.ACCEPTED));
                userLivedata.setValue(visibleProfile ?
                        new FSUser((QueryDocumentSnapshot) doc) : null);
            });
        });

        return userLivedata;
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
    private void updateFollow(String follower, String following, Follow.Status status,
                              ThenFunction successCallback,
                              CatchFunction failCallback) {
        WriteBatch batch = mDb.batch();
        DocumentReference followerRef = getUserSubCollectionRef(follower,
                FSFollow.FOLLOWING_COLLECTION.getName()).document(following);
        DocumentReference followingRef = getUserSubCollectionRef(following,
                FSFollow.FOLLOWER_COLLECTION.getName()).document(follower);
        if (status != Follow.Status.REJECTED) {
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
        updateFollow(getEmail(), email, Follow.Status.REQUESTED, successCallback, failCallback);
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
        updateFollow(getEmail(), email, Follow.Status.REJECTED, successCallback, failCallback);

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
        updateFollow(email, getEmail(), Follow.Status.ACCEPTED, successCallback, failCallback);
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
        updateFollow(email, getEmail(), Follow.Status.REJECTED, successCallback, failCallback);
    }
}
