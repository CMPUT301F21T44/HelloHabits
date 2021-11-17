package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.R;

/**
 * ViewModel for keeping track of the previous list screen
 * <p>
 * A Habit can be viewed from either the Today's Habits list or the All Habits list. This ViewModel
 * keeps track of the list that the Habit is viewed from so that the "Back" button for the View
 * Habit fragment works properly.
 */
public class PreviousListViewModel extends ViewModel {
    /**
     * LiveData object keeping track of the destination ID of the previous list screen
     */
    private final MutableLiveData<Integer> mDestinationId =
            new MutableLiveData<>(R.id.TodaysHabitsFragment);

    public LiveData<Integer> getDestinationId() {
        return mDestinationId;
    }

    public void setDestinationId(int id) {
        mDestinationId.setValue(id);
    }
}
