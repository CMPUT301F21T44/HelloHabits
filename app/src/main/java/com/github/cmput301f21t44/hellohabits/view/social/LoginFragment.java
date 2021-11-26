package com.github.cmput301f21t44.hellohabits.view.social;

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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Fragment for logging in or signing up
 */
public class LoginFragment extends Fragment {
    private static final int LONG_MESSAGE_THRESHOLD = 50;
    private FragmentLoginBinding binding;
    private ActionBar mActionBar;
    private Authentication auth;
    private FirebaseAuth mAuth;
    private NavController mNav;
    private boolean mIsLogin;

    /**
     * This function set the basic creation
     *
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        mAuth = FirebaseAuth.getInstance();
        mNav = NavHostFragment.findNavController(this);
        auth = new Authentication();
        mIsLogin = true;
        toggleNameFieldVisibility();
        binding.submit.setOnClickListener(v -> {
            if (mIsLogin) {
                login();
            } else {
                signup();
            }
        });
        binding.toggle.setOnClickListener(v -> toggleFunction());
    }

    /**
     * This function sets the visibility of name field by whether the user is login or sign-up
     */
    private void toggleNameFieldVisibility() {
        binding.name.setVisibility(mIsLogin ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * This function display proper message according to user's login or sign-up condition
     */
    private void toggleFunction() {
        mIsLogin = !mIsLogin;
        if (mIsLogin) {
            binding.toggle.setText(R.string.action_register_short);
            binding.submit.setText(R.string.action_sign_in_short);
            binding.guide1.setText("No account yet?");
            binding.guide2.setVisibility(View.VISIBLE);
        } else {
            binding.toggle.setText(R.string.action_sign_in_short);
            binding.submit.setText(R.string.action_register_short);
            binding.guide1.setText("          Back to");
            binding.guide2.setVisibility(View.INVISIBLE);
        }
        toggleNameFieldVisibility();
    }

    /**
     * This function handles the login event
     */
    private void login() {
        if (InputValidator.hasEmptyInput(binding.email, binding.password)) {
            showErrorToast("Failed to sign in", new Exception("There is an empty field"));
            return;
        }
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        auth.signIn(email, password)
                .addOnSuccessListener(authResult -> finish())
                .addOnFailureListener(e -> showErrorToast("Failed to sign in", e));
    }

    private void finish() {
        mActionBar.show();
        mNav.navigate(R.id.TodaysHabitsFragment);
    }

    /**
     * This function handles the sign-up event
     */
    private void signup() {
        if (InputValidator.hasEmptyInput(binding.name, binding.email, binding.password)) {
            showErrorToast("Failed to sign up", new Exception("There is an empty field"));
            return;
        }
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        auth.signup(name, email, password, this::finish,
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
    }

    /**
     * This function close the current page and return to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}