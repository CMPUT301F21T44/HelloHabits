package com.github.cmput301f21t44.hellohabits.view.habitevent;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentCreateEditHabitEventBinding;
import com.github.cmput301f21t44.hellohabits.firebase.FSLocation;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;

import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PhotoViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.LocationViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Fragment class for creating or editing a HabitEvent
 */
public class CreateEditHabitEventFragment extends Fragment {
    public static final int MAX_COMMENT_LEN = 20;

    public static final int REQUEST_CODE_CAMERA = 123123;
    public static final int REQUEST_CODE_GALLERY = 0;
    private ImageView eventImage;

    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private FragmentCreateEditHabitEventBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private HabitEvent mHabitEvent;
    private boolean isEdit;
    private NavController mNavController;

    private PhotoViewModel mPhotoViewModel;

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

        eventImage = binding.eventImage;
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

        if (getImageUri() == null) {
            mHabitEventViewModel.insert(habitId, comment, null, null,
                    () -> mNavController.navigate(R.id.ViewHabitFragment),
                    e -> showErrorToast("Failed to add habit event", e));
        } else {
            mPhotoViewModel.uploadPhoto(getImageUri(), () -> mHabitEventViewModel.insert(habitId, comment, getImageUri().getLastPathSegment(),
                    null, () -> mNavController.navigate(R.id.ViewHabitFragment),
                    e -> showErrorToast("Failed to add habit event", e)), e -> showErrorToast("Failed to upload photo", e));
        }


        Location loc;
        if (isEventLocationChanged) {
            loc = mlocationviewmodel.getLocation().getValue();
            mlocationviewmodel.setLocation(null);
        } else {
            loc = null;
        }
        mHabitEventViewModel.insert(habitId, comment, null, loc,
                () -> mNavController.navigate(R.id.ViewHabitFragment),
                e -> showErrorToast("Failed to add habit", e));
    }

    private Uri getImageUri() {
        return ((MainActivity) requireActivity()).getImageUri();
    }

    /**
     * Sets the image URI for the Habit Event
     *
     * @param path File path
     */
    private void setImageUri(String path) {
        File imageTemp = new File(requireActivity().getExternalCacheDir(), path);
        try {
            //noinspection ResultOfMethodCallIgnored
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > 24) {
            // contentProvider
            ((MainActivity) requireActivity()).setImageUri(
                    FileProvider.getUriForFile(requireContext(),
                            "com.github.cmput301f21t44.hellohabits.fileprovider", imageTemp));
        } else {
            ((MainActivity) requireActivity()).setImageUri(Uri.fromFile(imageTemp));
        }
    }

    /**
     * Updates an existing habit event
     *
     * @param comment Updated HabitEvent's comment
     */
    private void updateHabitEvent(String comment) {
        Location loc;
        if (isEventLocationChanged) {
            loc = new FSLocation(longitude, latitude, 0);
        } else {
            loc = mHabitEvent.getLocation();
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

        binding.buttonCamera.setOnClickListener(v -> getFromCamera());
        binding.buttonGallery.setOnClickListener(v -> getFromGallery());
    }

    /**
     * Get user's geolocation to attach to the event
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            mNavController.navigate(R.id.setLocationFragment);
        }
    }

    /**
     * Get a photo to attach to the event by taking a photo from camera
     */
    public void getFromCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // GOTO take photo if has permission
            doTakePhoto();
        } else {
            // ask for permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }

    }

    private String getDateString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
        Instant instant = Instant.now();
        return formatter.format(instant);
    }

    /**
     * Take photo
     */
    @SuppressLint("QueryPermissionsNeeded") // Permission is already granted at this point
    public void doTakePhoto() {
        String habitId = Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue())
                .getId();
        String newPicName = String.format("%s_%s.jpg", habitId, getDateString());
        setImageUri(newPicName);
        Log.println(Log.ASSERT, "DO TAKE PHOTO", String.format("uri: %s", getImageUri().getLastPathSegment()));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            requireActivity().startActivityFromFragment(this, intent, REQUEST_CODE_CAMERA);
        }
    }

    /**
     * Get a photo to attach to the event by choosing photo from gallery
     * TODO: Implement
     */
    private void getFromGallery() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    public void doChooseFromGallery() {
        // TODO
    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onStart method
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onStart() {
        super.onStart();
        // Observe the current event object being observed
        mHabitEventViewModel.getSelectedEvent().observe(getViewLifecycleOwner(),
                this::onHabitEventChanged);

        mPhotoViewModel = ViewModelFactory.getProvider(requireActivity()).get(PhotoViewModel.class);
        mPhotoViewModel.getTakePhoto().observe(requireActivity(), takePhoto -> {
            if (takePhoto) {
                doTakePhoto();
                mPhotoViewModel.setChoosePhoto(false);
            }
        });
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
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void onHabitEventChanged(HabitEvent habitEvent) {
        isEdit = habitEvent != null;
        if (isEdit) {
            // update UI
            mHabitEvent = habitEvent;
            binding.editTextComment.setText(habitEvent.getComment());

            if (habitEvent.getPhotoPath() != null) {
                setImageUri(habitEvent.getPhotoPath());
                Log.println(Log.ASSERT, "ON HABIT EVENT CHANGED", String.format("uri: %s", getImageUri().getLastPathSegment()));
                mPhotoViewModel.downloadPhoto(getImageUri(), this::setImageView,
                        e -> showErrorToast("Failed to download photo", e));
            }

            Location location = habitEvent.getLocation();
            if (location != null) {
                this.longitude = location.getLongitude();
                this.latitude = location.getLatitude();
                setLocationText();
            } else {
                binding.textView3.setText("Location: not set yet");
            }
        } else {
            binding.textView3.setText("Location: not set yet");
        }

        // Set title to the currently selected Habit
        String habitTitle =
                Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue()).getTitle();
        binding.habitTitle.setText(habitTitle);

    }

    @Override
    public void onPause() {
        super.onPause();
        mlocationviewmodel.setLocation(null);
    }

    @SuppressLint("SetTextI18n")
    private void setLocationText() {
        binding.textView3.setText("Location: (" + latitude + ", " + longitude + ")\nWith accuracy of within:" + 0 + " meters");
    }

    @Override
    public void onResume() {
        super.onResume();
        mlocationviewmodel.getLocation().observe(this, location -> {
            if (location == null) {
                this.isEventLocationChanged = false;
                return;
            }
            this.isEventLocationChanged = true;
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            setLocationText();
        });
    }

    /**
     * Sets the ImageView to the current URI
     */
    private void setImageView() {
        try {
            InputStream is = requireActivity().getContentResolver().openInputStream(getImageUri());
            eventImage.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (FileNotFoundException e) {
            showErrorToast("Failed to find photo", e);
        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // we're getting different request codes when using the MainActivity onActivity result :(
        // so we gotta stick with the deprecated API
        super.onActivityResult(requestCode, resultCode, data);
        Log.println(Log.ASSERT, "ON ACTIVITY RESULT", String.format("request code: %d", requestCode));
        Log.println(Log.ASSERT, "ON ACTIVITY RESULT", String.format("result code: %d", resultCode));
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                // obtain the photo taken
                setImageView();
            } else if (requestCode == REQUEST_CODE_GALLERY) {

//            if (Build.VERSION.SDK_INT < 19) {
//                ImageUtil.handleImageBeforeApi19(this, eventImage, data);
//            } else {
//                ImageUtil.handleImageOnApi19(this, eventImage, data);
//            }

            }
        }
    }




}