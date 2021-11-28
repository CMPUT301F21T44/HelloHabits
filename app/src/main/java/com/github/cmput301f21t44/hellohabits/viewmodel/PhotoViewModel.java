package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.habitevent.PhotoRepository;

import java.io.InputStream;

public class PhotoViewModel extends ViewModel {
    private final PhotoRepository mPhotoRepo;
    private final MutableLiveData<Boolean> mTakePhotoWithCamera = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mChoosePhotoFromGallery = new MutableLiveData<>(false);
    private final MutableLiveData<InputStream> mPhotoDone = new MutableLiveData<>();

    private final MutableLiveData<Uri> mPhotoUri = new MutableLiveData<>();

    public PhotoViewModel(PhotoRepository photoRepo) {
        this.mPhotoRepo = photoRepo;
    }

    public LiveData<Uri> getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(Uri value) {
        mPhotoUri.setValue(value);
    }


    public LiveData<Boolean> getTakePhoto() {
        return mTakePhotoWithCamera;
    }

    public void setTakePhoto(boolean value) {
        mTakePhotoWithCamera.setValue(value);
    }

    public LiveData<Boolean> getChoosePhoto() {
        return mChoosePhotoFromGallery;
    }

    public void setChoosePhoto(boolean value) {
        mChoosePhotoFromGallery.setValue(value);
    }


    public LiveData<InputStream> getPhotoDone() {
        return mPhotoDone;
    }

    public void setPhotoDone(InputStream inputStream) {
        mPhotoDone.setValue(inputStream);
    }

    public void uploadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback) {
        mPhotoRepo.uploadPhoto(file, successCallback, failCallback);
    }

    public void downloadPhoto(Uri file, ThenFunction successCallback, CatchFunction failCallback) {
        mPhotoRepo.downloadPhoto(file, successCallback, failCallback);
    }

}
