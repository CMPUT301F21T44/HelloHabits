package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.InputStream;

public class PhotoViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mTakePhotoWithCamera = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mChoosePhotoFromGallery = new MutableLiveData<>(false);
    private final MutableLiveData<InputStream> mPhotoDone = new MutableLiveData<>();

    private final MutableLiveData<Uri> mPhotoUri = new MutableLiveData<>();

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

}
