package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HabitMapLiveData extends MediatorLiveData<Map<String, FSHabit>> {
    private final MediatorLiveData<Map<String, LiveData<List<HabitEvent>>>>
            mHabitEventLiveDataMap = new MediatorLiveData<>();
    private final GetHabitEvents mListenCallback;

    public HabitMapLiveData(GetHabitEvents listenCallback) {
        this.mListenCallback = listenCallback;
    }

    public void mergeMap(Map<String, FSHabit> habitMap) {
        Map<String, FSHabit> oldMap = this.getValue();
        if (oldMap == null) {
            mHabitEventLiveDataMap.setValue(new HashMap<>());
            addSource(mHabitEventLiveDataMap, liveDataMap -> {
                if (liveDataMap.isEmpty()) return;
                for (Map.Entry<String, LiveData<List<HabitEvent>>> entry : liveDataMap.entrySet()) {
                    FSHabit habit = habitMap.get(entry.getKey());
                    if (habit == null) continue;
                    habit.setHabitEvents(entry.getValue().getValue());
                    habitMap.put(entry.getKey(), habit);
                    this.setValue(habitMap);
                }
            });

        }
        setValue(habitMap);
        addEventLiveData();
    }

    private void addEventLiveData() {
        for (LiveData<List<HabitEvent>> eventList :
                Objects.requireNonNull(mHabitEventLiveDataMap.getValue()).values()) {
            mHabitEventLiveDataMap.removeSource(eventList);
        }
        mHabitEventLiveDataMap.setValue(new HashMap<>());

        for (String habitId : Objects.requireNonNull(this.getValue()).keySet()) {
            LiveData<List<HabitEvent>> events = this.mListenCallback.getHabitEventLiveData(habitId);
            mHabitEventLiveDataMap.removeSource(events);
            mHabitEventLiveDataMap.addSource(events, habitEvents -> {
                Map<String, LiveData<List<HabitEvent>>> habitEventLiveDataMap =
                        mHabitEventLiveDataMap.getValue();
                if (habitEventLiveDataMap == null)
                    habitEventLiveDataMap = new HashMap<>();

                habitEventLiveDataMap.put(habitId, events);
                mHabitEventLiveDataMap.setValue(habitEventLiveDataMap);
            });
        }
    }

    interface GetHabitEvents {
        LiveData<List<HabitEvent>> getHabitEventLiveData(String habitId);
    }
}
