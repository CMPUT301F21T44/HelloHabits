package com.github.cmput301f21t44.hellohabits.view.social;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentLoginBinding;
import com.github.cmput301f21t44.hellohabits.firebase.Authentication;
import com.github.cmput301f21t44.hellohabits.view.InputValidator;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.Objects;

/**
 * Fragment for logging in or signing up
 */
public class LoginFragment extends Fragment {
    public static final String HELLO_HABITS_SHARED_PREFERENCES = "HELLO_HABITS_RECORD";
    public static final String NAME = "name";
    private static final int LONG_MESSAGE_THRESHOLD = 50;
    private static final String REMEMBER_PASSWORD = "rememberPassword";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private FragmentLoginBinding mBinding;
    private ActionBar mActionBar;
    private Authentication mAuth;
    private NavController mNav;
    private boolean mIsLogin;
    private boolean mRememberPassword;
    private SharedPreferences mPreferences;

    private void loadPreferences() {
        mPreferences = requireActivity()
                .getSharedPreferences(HELLO_HABITS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mRememberPassword = mPreferences.getBoolean(REMEMBER_PASSWORD, false);
        String email = mPreferences.getString(EMAIL, "");
        String password = mPreferences.getString(PASSWORD, "");

        mBinding.email.setText(email);
        if (mRememberPassword) {
            mBinding.password.setText(password);
        }
        mBinding.rememberPassword.setChecked(mRememberPassword);
    }

    private void savePreferences(String name, String email, String password) {
        SharedPreferences.Editor editPreferences = mPreferences.edit();
        mRememberPassword = mBinding.rememberPassword.isChecked();
        editPreferences.putBoolean(REMEMBER_PASSWORD, mRememberPassword);
        editPreferences.putString(NAME, name);
        editPreferences.putString(EMAIL, email);
        editPreferences.putString(PASSWORD, mRememberPassword ? password : "");
        editPreferences.apply();
    }

    /**
     * When the view is created, connect the layout to the class using binding
     *
     * @param inflater           a default LayoutInflater
     * @param container          a default ViewGroup
     * @param savedInstanceState a default Bundle
     * @return a path representing the root component of the corresponding layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**
     * This function handle the login and sign-up
     * If the input has already in fire store do login,else go sign-up
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNav = NavHostFragment.findNavController(this);
        mAuth = new Authentication();
        mIsLogin = true;
        toggleNameFieldVisibility();
        mBinding.submit.setOnClickListener(v -> {
            if (mIsLogin) {
                login();
            } else {
                signup();
            }
        });
        mBinding.toggle.setOnClickListener(v -> toggleFunction());
    }

    /**
     * This function sets the visibility of name field by whether the user is login or sign-up
     */
    private void toggleNameFieldVisibility() {
        mBinding.name.setVisibility(mIsLogin ? INVISIBLE : VISIBLE);
    }

    /**
     * This function display proper message according to user's login or sign-up condition
     */
    private void toggleFunction() {
        mIsLogin = !mIsLogin;
        if (mIsLogin) {
            mBinding.toggle.setText(R.string.action_register_short);
            mBinding.submit.setText(R.string.action_sign_in_short);
            mBinding.guide1.setText(R.string.no_account_yet);
            mBinding.guide2.setVisibility(VISIBLE);
        } else {
            mBinding.toggle.setText(R.string.action_sign_in_short);
            mBinding.submit.setText(R.string.action_register_short);
            mBinding.guide1.setText(R.string.back_to);
            mBinding.guide2.setVisibility(INVISIBLE);
        }
        toggleNameFieldVisibility();
    }

    /**
     * This function handles the login event
     */
    private void login() {
        if (InputValidator.hasEmptyInput(mBinding.email, mBinding.password)) {
            showErrorToast("Failed to sign in", new Exception("There is an empty field"));
            return;
        }
        String email = mBinding.email.getText().toString();
        String password = mBinding.password.getText().toString();

        mAuth.signIn(email, password)
                .addOnSuccessListener(authResult -> finish(email, password))
                .addOnFailureListener(e -> showErrorToast("Failed to sign in", e));
    }

    private void finish(String email, String password) {
        UserViewModel viewModel = ViewModelFactory.getProvider(requireActivity())
                .get(UserViewModel.class);
        viewModel.getCurrentUser().observe(requireActivity(), user -> {
            mActionBar.show();
            savePreferences(user.getName(), email, password);
            mNav.navigate(R.id.TodaysHabitsFragment);
        });
    }

    /**
     * This function handles the sign-up event
     */
    private void signup() {
        if (InputValidator.hasEmptyInput(mBinding.name, mBinding.email, mBinding.password)) {
            showErrorToast("Failed to sign up", new Exception("There is an empty field"));
            return;
        }
        String name = mBinding.name.getText().toString();
        String email = mBinding.email.getText().toString();
        String password = mBinding.password.getText().toString();
        mAuth.signup(name, email, password, () -> finish(email, password),
                e -> showErrorToast("Failed to sign up", e));
    }

    /**
     * Show error message as Toast
     *
     * @param text Text to output
     * @param e    Exception thrown
     */
    private void showErrorToast(String text, Exception e) {
        String message = text + ": " + e.getLocalizedMessage();
        int duration = message.length() > LONG_MESSAGE_THRESHOLD
                ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(requireContext(), message, duration).show();
    }

    /**
     * This function handle the back button event when the fragment is on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.TodaysHabitsFragment);
        }
        mActionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity())
                .getSupportActionBar());
        // Remove action bar on login screen
        mActionBar.hide();

        loadPreferences();
    }

    /**
     * This function close the current page and return to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}