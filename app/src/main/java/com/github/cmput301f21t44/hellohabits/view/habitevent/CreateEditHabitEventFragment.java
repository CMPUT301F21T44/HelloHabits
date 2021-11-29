package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentCreateEditHabitEventBinding;
import com.github.cmput301f21t44.hellohabits.firebase.FSLocation;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;
import com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.LocationViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

/**
 * Fragment class for creating or editing a HabitEvent
 */
public class CreateEditHabitEventFragment extends Fragment {
    public static final int MAX_COMMENT_LEN = 20;
    private FragmentCreateEditHabitEventBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private HabitEvent mHabitEvent;
    private boolean isEdit;
    private NavController mNavController;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private LocationViewModel mlocationviewmodel;
    private double latitude;
    private double longitude;
    private boolean isEventLocationChanged;


    /**
     * When the view is created, connect the layout to the class using binding
     *
     * @param inflater           a default LayoutInflater
     * @param container          a default ViewGroup
     * @param savedInstanceState a default Bundle
     * @return a path representing the root component of the corresponding layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateEditHabitEventBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     * Show error message on the bottom of the screen
     *
     * @param text  Message to print out
     * @param error Error received
     */
    private void showErrorToast(String text, Exception error) {
        Toast.makeText(requireActivity(), text + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates and returns the input from the comment field
     *
     * @return A String if input is valid, null if invalid
     */
    private String validateComment() {
        String comment = binding.editTextComment.getText().toString();
        if (comment.length() > MAX_COMMENT_LEN) {
            binding.editTextComment.setError(CreateEditHabitFragment.TOO_LONG_ERROR_MESSAGE);
            binding.editTextComment.requestFocus();
            return null;
        }
        return comment;
    }

    /**
     * Creates or edits a HabitEvent depending on the current HabitEvent data
     */
    private void submitHabitEvent() {
        String comment = validateComment();

        // invalid comment
        if (comment == null) return;

        if (isEdit) {
            updateHabitEvent(comment);
        } else {
            createHabitEvent(comment);
        }
    }

    /**
     * Creates a new habit event
     *
     * @param comment New HabitEvent's comment
     */
    private void createHabitEvent(String comment) {
        String habitId = Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue())
                .getId();
        FSLocation loc;
        if(isEventLocationChanged){
            loc = new FSLocation(longitude,latitude,0);
            mlocationviewmodel.setIsLocationChanged(false);
        }
        else {
            loc = null;
        }
        mHabitEventViewModel.insert(habitId, comment, null, loc,
                () -> mNavController.navigate(R.id.ViewHabitFragment),
                e -> showErrorToast("Failed to add habit", e));

    }

    /**
     * Updates an existing habit event
     *
     * @param comment Updated HabitEvent's comment
     */
    private void updateHabitEvent(String comment) {
        FSLocation loc;
        if(isEventLocationChanged){
            loc = new FSLocation(longitude,latitude,0);
            mlocationviewmodel.setIsLocationChanged(false);
        }
        else {
            loc = null;
        }
        mHabitEventViewModel.update(mHabitEvent.getId(),
                mHabitEvent.getHabitId(), mHabitEvent.getDate(), comment,
                null, loc,
                (updatedHabitEvent) -> {
                    mHabitEventViewModel.setSelectedEvent(updatedHabitEvent);
                    mNavController.navigate(R.id.ViewHabitFragment);
                },
                (e) -> showErrorToast("Failed to update habit event", e));
    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes member variables and button OnClickListeners
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mlocationviewmodel = provider.get(LocationViewModel.class);
        mHabitViewModel = provider.get(HabitViewModel.class);
        mHabitEventViewModel = provider.get(HabitEventViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabitEvent.setOnClickListener(v -> submitHabitEvent());
        binding.buttonAddLocation.setOnClickListener(v -> getLocation());

        binding.buttonAddPhoto.setOnClickListener(v -> getPhoto());
    }

    /**
     * Get user's geolocation to attach to the event
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else {
            mNavController.navigate(R.id.setLocationFragment);
        }
    }

    /**
     * Get a photo to attach to the event
     * TODO: Implement
     */
    private void getPhoto() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onStart method
     */
    @Override
    public void onStart() {
        super.onStart();
        // Observe the current event object being observed
        mHabitEventViewModel.getSelectedEvent().observe(getViewLifecycleOwner(),
                this::onHabitEventChanged);
    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onDestroyView method
     * <p>
     * Unbinds the view binding
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Updates the UI to the currently observed HabitEvent
     *
     * @param habitEvent The current HabitEvent to be shown on screen, null for new HabitEvent
     */
    private void onHabitEventChanged(HabitEvent habitEvent) {
        isEdit = habitEvent != null;
        if (isEdit) {
            // update UI
            mHabitEvent = habitEvent;
            binding.editTextComment.setText(habitEvent.getComment());
            String locationText;
            if(habitEvent.getLocation() == null){
                locationText = "Location: not set yet";
            }
            else{
                locationText = "Location: ("+habitEvent.getLocation().getLatitude()+", "+habitEvent.getLocation().getLongitude()+
                        ")\nWith accuracy of within:"+habitEvent.getLocation().getAccuracy()+" meters";
            }
            binding.textView3.setText(locationText);
        }

        // Set title to the currently selected Habit
        String habitTitle =
                Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue()).getTitle();
        binding.habitTitle.setText(habitTitle);

    }
    @Override
    public void onResume() {
        super.onResume();
        mlocationviewmodel.getMlongitude().observe(this, longitude ->this.longitude=longitude);
        mlocationviewmodel.getMlatitude().observe(this, latitude ->this.latitude=latitude);
        mlocationviewmodel.getIsLocationChanged().observe(this, isEventLocationChanged ->this.isEventLocationChanged=isEventLocationChanged);
    }

}