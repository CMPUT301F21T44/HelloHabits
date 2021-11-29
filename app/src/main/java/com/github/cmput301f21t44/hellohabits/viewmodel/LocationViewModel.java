package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;


public class LocationViewModel extends ViewModel {
    public MutableLiveData<Double> getMlatitude() {
        return mlatitude;
    }

    public LocationViewModel() {
        isLocationChanged.setValue(false);
    }

    public MutableLiveData<Double> getMlongitude() {
        return mlongitude;
    }

    public MutableLiveData<Double> getMaccuracy() {
        return maccuracy;
    }

    public void setMlatitude(Double latitude) {
        mlatitude.setValue(latitude);
    }

    public void setMlongitude(Double longitude) {
        mlongitude.setValue(longitude);
    }

    public MutableLiveData<Boolean> getIsLocationChanged() {
        return isLocationChanged;
    }

    public void setIsLocationChanged(Boolean locationChanged) {
        isLocationChanged.setValue(locationChanged);
    }

    public void setMaccuracy(Double accuracy) {
        maccuracy.setValue(accuracy);
    }

    private final MutableLiveData<Double> mlatitude = new MutableLiveData<>();
    private final MutableLiveData<Double> mlongitude = new MutableLiveData<>();
    private final MutableLiveData<Double> maccuracy = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLocationChanged = new MutableLiveData<>();


}
