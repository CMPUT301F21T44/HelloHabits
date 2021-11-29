package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;


public class LocationViewModel extends ViewModel {
    private final MutableLiveData<Double> mlatitude = new MutableLiveData<>();
    private final MutableLiveData<Double> mlongitude = new MutableLiveData<>();
    private final MutableLiveData<Double> maccuracy = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLocationChanged = new MutableLiveData<>();

    private final MutableLiveData<Location> mLocation = new MutableLiveData<>();

    public LocationViewModel() {
        isLocationChanged.setValue(false);
    }

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation.setValue(location);
    }

    public MutableLiveData<Double> getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(Double latitude) {
        mlatitude.setValue(latitude);
    }

    public MutableLiveData<Double> getMlongitude() {
        return mlongitude;
    }

    public void setMlongitude(Double longitude) {
        mlongitude.setValue(longitude);
    }

    public MutableLiveData<Double> getMaccuracy() {
        return maccuracy;
    }

    public void setMaccuracy(Double accuracy) {
        maccuracy.setValue(accuracy);
    }

    public MutableLiveData<Boolean> getIsLocationChanged() {
        return isLocationChanged;
    }

    public void setIsLocationChanged(Boolean locationChanged) {
        isLocationChanged.setValue(locationChanged);
    }


}
