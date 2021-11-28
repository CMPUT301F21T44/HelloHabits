package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PhotoViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mTakePhotoWithCamera = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mChoosePhotoFromGallery = new MutableLiveData<>(false);

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
}
