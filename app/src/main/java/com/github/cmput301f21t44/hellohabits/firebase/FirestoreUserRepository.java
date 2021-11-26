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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        MediatorLiveData<List<User>> usersListLiveData = new MediatorLiveData<>();
        MediatorLiveData<HashMap<String, FSUser>> usersMapLiveData = new MediatorLiveData<>();

        LiveData<List<Follow>> followingLiveData = getFollowLiveData(FSFollow.FOLLOWING_COLLECTION);
        LiveData<List<Follow>> followerLiveData = getFollowLiveData(FSFollow.FOLLOWER_COLLECTION);

        usersListLiveData.addSource(usersMapLiveData, userMap ->
                usersListLiveData.setValue(new ArrayList<>(userMap.values())));

        getAllUsersCollectionRef().addSnapshotListener((userSnapshots, err) -> {
            if (userSnapshots == null) return;
            final HashMap<String, FSUser> users = new HashMap<>();
            for (QueryDocumentSnapshot doc : userSnapshots) {
                final FSUser user = new FSUser(doc);
                // exclude self from list
                if (user.getEmail().equals(getEmail())) continue;
                users.put(user.getEmail(), user);
            }
            usersMapLiveData.setValue(users);
        });

        usersMapLiveData.addSource(followingLiveData, following -> {
            if (following == null) return;
            final HashMap<String, FSUser> users = usersMapLiveData.getValue();
            if (users == null) return;
            for (Follow follow : following)
                Objects.requireNonNull(users.get(follow.getEmail())).setFollowingStatus(follow.getStatus());

            usersMapLiveData.setValue(users);
        });

        usersMapLiveData.addSource(followerLiveData, follower -> {
            if (follower == null) return;
            final HashMap<String, FSUser> users = usersMapLiveData.getValue();
            if (users == null) return;
            for (Follow follow : follower)
                Objects.requireNonNull(users.get(follow.getEmail())).setFollowerStatus(follow.getStatus());

            usersMapLiveData.setValue(users);
        });


        return usersListLiveData;
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
