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
     * Get LiveData for a follow
     *
     * @param email      The user's email
     * @param collection The follow collection to fetch from
     * @return LiveData of an FSFollow
     */
    private LiveData<FSFollow> getFollowLiveData(String email,
                                                 FSFollow.FollowCollection collection) {
        MutableLiveData<FSFollow> followLiveData = new MutableLiveData<>();
        getUserSubCollectionRef(email, collection.toString())
                .document(getEmail()).addSnapshotListener((follow, e) -> {
            if (follow == null) return;
            if (follow.exists()) {
                followLiveData.setValue(new FSFollow(follow));
            }
        });

        return followLiveData;
    }

    private LiveData<List<Follow>> getFollowLiveData(FSFollow.FollowCollection collection) {
        MutableLiveData<List<Follow>> followLiveData = new MutableLiveData<>();
        getUserSubCollectionRef(getEmail(), collection.toString())
                .addSnapshotListener((follows, e) -> {
                    if (follows == null) return;
                    List<Follow> followList = new ArrayList<>();
                    for (QueryDocumentSnapshot follow : follows) {
                        followList.add(new FSFollow(follow));
                    }
                    followLiveData.setValue(followList);
                });

        return followLiveData;
    }

    @Override
    public LiveData<List<Follow>> getFollowers() {
        return getFollowLiveData(FSFollow.FOLLOWER_COLLECTION);
    }

    @Override
    public LiveData<List<Follow>> getFollowing() {
        return getFollowLiveData(FSFollow.FOLLOWING_COLLECTION);
    }

    @Override
    public LiveData<List<User>> getAllUsers() {
        MediatorLiveData<List<User>> usersLiveData = new MediatorLiveData<>();
        getAllUsersCollectionRef().addSnapshotListener((userSnapshots, err) -> {
            if (userSnapshots == null) return;
            final List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot doc : userSnapshots) {
                final FSUser user = new FSUser(doc);
                // exclude self from list
                if (user.getEmail().equals(getEmail())) continue;
                LiveData<FSFollow> followingLiveData = getFollowLiveData(user.getEmail(),
                        FSFollow.FOLLOWING_COLLECTION);
                LiveData<FSFollow> followerLiveData = getFollowLiveData(user.getEmail(),
                        FSFollow.FOLLOWER_COLLECTION);
                usersLiveData.addSource(followingLiveData, fsFollow ->
                        user.setFollowerStatus(fsFollow.getStatus()));
                usersLiveData.addSource(followerLiveData, fsFollow ->
                        user.setFollowingStatus(fsFollow.getStatus()));
                users.add(user);
            }
            usersLiveData.setValue(users);
        });
        return usersLiveData;
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
                FSFollow.FOLLOWING_COLLECTION.toString()).document(following);
        DocumentReference followingRef = getUserSubCollectionRef(following,
                FSFollow.FOLLOWER_COLLECTION.toString()).document(follower);
        if (status != Follow.Status.REJECTED) {
            // will either create or update
            batch.set(followerRef, new FSFollow(following, status).getMap(), SetOptions.merge());
            batch.set(followingRef, new FSFollow(follower, status).getMap(), SetOptions.merge());
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
