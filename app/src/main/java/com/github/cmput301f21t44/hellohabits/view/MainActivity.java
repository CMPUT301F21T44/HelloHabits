package com.github.cmput301f21t44.hellohabits.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ActivityMainBinding;
import com.github.cmput301f21t44.hellohabits.firebase.Authentication;
import com.github.cmput301f21t44.hellohabits.view.habitevent.CreateEditHabitEventFragment;
import com.github.cmput301f21t44.hellohabits.viewmodel.PhotoViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private PhotoViewModel mPhotoViewModel;
    private boolean mFromViewHabit;
    private ActivityMainBinding mBinding;
    private Authentication mAuth;
    private Uri mImageUri;

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri uri) {
        this.mImageUri = uri;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null) {
            outState.putString("cameraImageUri", mImageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            mImageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.appBarMain.toolbar);
        mAuth = new Authentication();

        ViewModelProvider provider = ViewModelFactory.getProvider(this);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        mPhotoViewModel = provider.get(PhotoViewModel.class);

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
        final TextView helloMessage = mBinding.navView.getHeaderView(0)
                .findViewById(R.id.hello_message);
        if (helloMessage == null) return;
        helloMessage.setText(message.isEmpty() ? "Hello Habits" : message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CreateEditHabitEventFragment.REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPhotoViewModel.setTakePhoto(true);
            } else {
                Toast.makeText(this, "We have no permission to camera! (T▽T) ...", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CreateEditHabitEventFragment.REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPhotoViewModel.setChoosePhoto(true);
            } else {
                Toast.makeText(this, "We have no permission to gallery! (T▽T) ...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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