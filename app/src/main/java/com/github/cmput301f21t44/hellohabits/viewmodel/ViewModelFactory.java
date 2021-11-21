package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.cmput301f21t44.hellohabits.firebase.FirestoreEventRepository;
import com.github.cmput301f21t44.hellohabits.firebase.FirestoreHabitRepository;
import com.github.cmput301f21t44.hellohabits.firebase.FirestoreUserRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;
import com.github.cmput301f21t44.hellohabits.model.UserRepository;

/**
 * Factory class for creating ViewModel instances
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile ViewModelFactory sInstance;

    private final HabitRepository mHabitRepository;
    private final HabitEventRepository mHabitEventRepository;
    private final UserRepository mUserRepository;

    /**
     * Used for testing dependency injection
     *
     * @param habitRepository      HabitRepository instance to insert to ViewModel
     * @param habitEventRepository HabitEventRepository instance to insert to ViewModel
     */
    public ViewModelFactory(HabitRepository habitRepository,
                            HabitEventRepository habitEventRepository,
                            UserRepository userRepository) {
        this.mHabitRepository = habitRepository;
        this.mHabitEventRepository = habitEventRepository;
        this.mUserRepository = userRepository;
    }

    /**
     * Creates a ViewModelFactory using the FireStore repositories' default constructors
     */
    private ViewModelFactory() {
        this(new FirestoreHabitRepository(), new FirestoreEventRepository(),
                new FirestoreUserRepository());
    }

    /**
     * Creates a ViewModelProvider for the given FragmentActivity
     *
     * @param activity FragmentActivity that will use the ViewModelFactory
     * @return ViewModelProvider that will provide ViewModels
     */
    public static ViewModelProvider getProvider(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance();
        return new ViewModelProvider(activity, factory);
    }

    /**
     * Gets the singleton instance of ViewModelFactory
     *
     * @return static instance of ViewModelFactory
     */
    public static ViewModelFactory getInstance() {
        synchronized (ViewModelFactory.class) {
            if (sInstance == null) {
                sInstance = new ViewModelFactory();
            }
        }
        return sInstance;
    }

    /**
     * Creates an instance of a given ViewModel class
     *
     * @param modelClass ViewModel class to instantiate
     * @param <T>        Type of ViewModel class
     * @return an instance of the given ViewModel class
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HabitViewModel.class)) {
            return (T) new HabitViewModel(mHabitRepository);
        } else if (modelClass.isAssignableFrom(HabitEventViewModel.class)) {
            return (T) new HabitEventViewModel(mHabitEventRepository);
        } else if (modelClass.isAssignableFrom(PreviousListViewModel.class)) {
            return (T) new PreviousListViewModel();
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
