package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.cmput301f21t44.hellohabits.db.habit.HabitEntityRepository;
import com.github.cmput301f21t44.hellohabits.db.habitevent.HabitEventEntityRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile ViewModelFactory INSTANCE;

    private final HabitRepository mHabitRepository;
    private final HabitEventRepository mHabitEventRepository;

    private ViewModelFactory(Application application) {
        mHabitRepository = new HabitEntityRepository(application);
        mHabitEventRepository = new HabitEventEntityRepository(application);
    }

    public static ViewModelProvider getProvider(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return new ViewModelProvider(activity, factory);
    }

    public static ViewModelFactory getInstance(Application application) {
        synchronized (ViewModelFactory.class) {
            if (INSTANCE == null) {
                INSTANCE = new ViewModelFactory(application);
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HabitViewModel.class)) {
            //noinspection unchecked
            return (T) new HabitViewModel(mHabitRepository);
        } else if (modelClass.isAssignableFrom(HabitEventViewModel.class)) {
            //noinspection unchecked
            return (T) new HabitEventViewModel(mHabitEventRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
