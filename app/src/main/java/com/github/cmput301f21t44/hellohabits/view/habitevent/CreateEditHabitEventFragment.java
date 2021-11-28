package com.github.cmput301f21t44.hellohabits.view.habitevent;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PhotoViewModel;
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
    public static final String TAG = "tag";
    public Uri imageUri;
    public String imageBase64;
    public ImageView eventImage;
    private FragmentCreateEditHabitEventBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private HabitEvent mHabitEvent;
    private boolean isEdit;
    private NavController mNavController;
    private PhotoViewModel mPhotoViewModel;
    private String mPicName;

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
        if (imageUri == null) {
            mHabitEventViewModel.insert(habitId, comment, null, null,
                    () -> mNavController.navigate(R.id.ViewHabitFragment),
                    e -> showErrorToast("Failed to add habit event", e));
        } else {
            mPhotoViewModel.uploadPhoto(imageUri, () -> {
                mHabitEventViewModel.insert(habitId, comment, imageUri.getLastPathSegment(),
                        null, () -> mNavController.navigate(R.id.ViewHabitFragment),
                        e -> showErrorToast("Failed to add habit event", e));
            }, e -> showErrorToast("Failed to upload photo", e));
        }
    }

    /**
     * Updates an existing habit event
     *
     * @param comment Updated HabitEvent's comment
     */
    private void updateHabitEvent(String comment) {
        mHabitEventViewModel.update(mHabitEvent.getId(),
                mHabitEvent.getHabitId(), mHabitEvent.getDate(), comment,
                null, null,
                (updatedHabitEvent) -> {
                    mHabitEventViewModel.setSelectedEvent(updatedHabitEvent);
                    mNavController.navigate(R.id.ViewHabitFragment);
                },
                (e) -> showErrorToast("Failed to update habit", e));
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
     * TODO: Implement
     */
    private void getLocation() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
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

    public void doTakePhoto() {
        String habitId = Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue())
                .getId();
        mPicName = String.format("%s_%s.jpg", habitId, getDateString());
        File imageTemp = new File(requireActivity().getExternalCacheDir(), mPicName);
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > 24) {
            // contentProvider
            imageUri = FileProvider.getUriForFile(requireContext(),
                    "com.github.cmput301f21t44.hellohabits.fileprovider", imageTemp);
            mPhotoViewModel.setPhotoUri(imageUri);
        } else {
            imageUri = Uri.fromFile(imageTemp);
            mPhotoViewModel.setPhotoUri(imageUri);
        }
        Log.println(Log.ASSERT, "DO TAKE PHOTO", String.format("uri: %s", imageUri.getLastPathSegment()));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hasStoragePermission(REQUEST_CODE_CAMERA)) {
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                requireActivity().startActivityFromFragment(this, intent, REQUEST_CODE_CAMERA);
            }
        }
    }

    private boolean hasStoragePermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED &&
                    requireContext().checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;

        }
    }

    /**
     * Get a photo to attach to the event by choosing photo from gallery
     * TODO: Implement
     */
    private void getFromGallery() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    public void doChsGly() {

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
        mPhotoViewModel.getPhotoDone().observe(requireActivity(), inputStream -> {
            try {
                Log.println(Log.ASSERT, "observer", String.format("bytes - %d", inputStream.available()));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                eventImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
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
    private void onHabitEventChanged(HabitEvent habitEvent) {
        isEdit = habitEvent != null;
        if (isEdit) {
            // update UI
            mHabitEvent = habitEvent;
            binding.editTextComment.setText(habitEvent.getComment());
            if (habitEvent.getPhotoPath() != null) {
                imageUri = Uri.fromFile(new File(habitEvent.getPhotoPath()));
                Log.println(Log.ASSERT, "ON HABIT EVENT CHANGED", String.format("uri: %s", imageUri.getLastPathSegment()));
                mPhotoViewModel.downloadPhoto(imageUri, () -> {
                    try {
                        InputStream is = requireActivity().getContentResolver().openInputStream(imageUri);
                        eventImage.setImageBitmap(BitmapFactory.decodeStream(is));
                    } catch (FileNotFoundException e) {
                        showErrorToast("Failed to create file for photo", e);
                    }
                }, e -> showErrorToast("Failed to download photo", e));
            }
        }

        // Set title to the currently selected Habit
        String habitTitle =
                Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue()).getTitle();
        binding.habitTitle.setText(habitTitle);
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
                try {
                    InputStream is = requireActivity().getContentResolver().openInputStream(imageUri);
                    eventImage.setImageBitmap(BitmapFactory.decodeStream(is));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_GALLERY) {

//            if (Build.VERSION.SDK_INT < 19) {
//                ImageUtil.handleImageBeforeApi19(this, eventImage, data);
//            } else {
//                ImageUtil.handleImageOnApi19(this, eventImage, data);
//            }

        }
    }
}
