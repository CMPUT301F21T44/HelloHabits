package com.github.cmput301f21t44.hellohabits.model.habitevent;

import android.net.Uri;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;

/**
 * Interface for dealing with photo files
 */
public interface PhotoRepository {
    /**
     * Uploads a photo to the repository
     *
     * @param file            Uri of the photo to upload
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void uploadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Downloads a photo from the repository
     *
     * @param file            Uri of the photo to download into
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void downloadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Deletes a photo from the repository
     *
     * @param file            Uri of the photo to be deleted
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void deletePhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback);
}
