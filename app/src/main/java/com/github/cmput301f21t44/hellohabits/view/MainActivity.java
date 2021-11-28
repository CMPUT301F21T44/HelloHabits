package com.github.cmput301f21t44.hellohabits.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ActivityMainBinding;
import com.github.cmput301f21t44.hellohabits.firebase.Authentication;
import com.github.cmput301f21t44.hellohabits.view.habitevent.CreateEditHabitEventFragment;
import com.github.cmput301f21t44.hellohabits.view.habitevent.ImageUtil;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;


import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private boolean mFromViewHabit;
    private ActivityMainBinding mBinding;
    private Authentication mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.appBarMain.toolbar);
        mAuth = new Authentication();

        mPreviousListViewModel = ViewModelFactory.getProvider(this).get(PreviousListViewModel.class);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.TodaysHabitsFragment,
                R.id.AllHabitsFragment,
                R.id.FollowersFragment,
                R.id.FollowingFragment,
                R.id.AllUsersFragment
        )
                .setOpenableLayout(mBinding.drawerLayout)
                .build();

        mNavController = Navigation
                .findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI
                .setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, mNavController);

        mPreviousListViewModel.getFromViewHabit()
                .observe(this, val -> this.mFromViewHabit = val);
        requireUser();
    }

    /**
     * Navigates to login if there is no user
     */
    private void requireUser() {
        if (mAuth.getCurrentUser() == null) {
            mNavController.navigate(R.id.LoginFragment);
        }
    }

    /**
     * Sets the header message in the sidebar
     *
     * @param message The message to add in the header
     */
    public void setHeaderMessage(String message) {
        // no way to do this with data binding :(
        final TextView helloMessage = (TextView) mBinding.navView.getHeaderView(0)
                .findViewById(R.id.hello_message);
        if (helloMessage == null) return;
        helloMessage.setText(message.isEmpty() ? "Hello Habits" : message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CreateEditHabitEventFragment createEditHabitEventFragment = (CreateEditHabitEventFragment) getSupportFragmentManager().findFragmentById(R.id.EventCreateEditFragment);

        if (requestCode == createEditHabitEventFragment.REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createEditHabitEventFragment.doTakePhoto();
            } else {
                Toast.makeText(this, "We have no permission to camera! (T▽T) ...", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == createEditHabitEventFragment.REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createEditHabitEventFragment.doChsGly();
            } else {
                Toast.makeText(this, "We have no permission to gallery! (T▽T) ...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CreateEditHabitEventFragment createEditHabitEventFragment = (CreateEditHabitEventFragment) getSupportFragmentManager().findFragmentById(R.id.EventCreateEditFragment);

        if (requestCode == createEditHabitEventFragment.REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                // obtain the photo taken
                try {
                    InputStream inputStream = getContentResolver().openInputStream(createEditHabitEventFragment.imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    createEditHabitEventFragment.eventImage.setImageBitmap(bitmap);
                    String imageToBase64 = ImageUtil.imageToBase64(bitmap);
                    createEditHabitEventFragment.imageBase64 = imageToBase64;
                } catch (FileNotFoundException e) {

                }
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE) {

            if (Build.VERSION.SDK_INT < 19) {
                handleImageBeforeApi19(data);
            } else {
                handleImageOnApi19(data);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            showSignOutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mFromViewHabit) {
            getOnBackPressedDispatcher().onBackPressed();
            mPreviousListViewModel.setFromViewHabit(false);
            return true;
        }

        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Show an AlertDialog when trying to sign out
     */
    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YES", (dialog, b) -> signOut())
                .setNegativeButton("NO", null).show();
    }

    /**
     * Signs out and restarts MainActivity to clear all LiveData listeners
     */
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}