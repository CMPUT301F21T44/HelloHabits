package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.R;

public class PreviousListViewModel extends ViewModel {
    private final MutableLiveData<Integer> destId =
            new MutableLiveData<>(R.id.TodaysHabitsFragment);

    public void select(int id) {
        destId.setValue(id);
    }

    public LiveData<Integer> getDestId() {
        return destId;
    }
}
