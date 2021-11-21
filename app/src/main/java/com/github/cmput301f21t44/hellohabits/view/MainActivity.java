package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ActivityMainBinding;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private boolean mFromViewHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        mPreviousListViewModel = ViewModelFactory.getProvider(this)
                .get(PreviousListViewModel.class);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.TodaysHabitsFragment,
                R.id.AllHabitsFragment
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

        mNavController = Navigation
                .findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI
                .setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, mNavController);

        mPreviousListViewModel.getFromViewHabit()
                .observe(this, val -> this.mFromViewHabit = val);
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

        if (id == R.id.action_settings) return true;

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
}