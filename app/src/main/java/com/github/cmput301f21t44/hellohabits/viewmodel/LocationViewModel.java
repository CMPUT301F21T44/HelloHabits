package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;


public class LocationViewModel extends ViewModel {
    private final MutableLiveData<Location> mLocation = new MutableLiveData<>();

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation.setValue(location);
    }

}
