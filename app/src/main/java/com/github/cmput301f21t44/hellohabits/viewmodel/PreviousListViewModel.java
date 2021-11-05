package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.R;

public class PreviousListViewModel extends ViewModel {
    private final MutableLiveData<Integer> mDestinationId =
            new MutableLiveData<>(R.id.TodaysHabitsFragment);

    public LiveData<Integer> getDestinationId() {
        return mDestinationId;
    }

    public void setDestinationId(int id) {
        mDestinationId.setValue(id);
    }
}
