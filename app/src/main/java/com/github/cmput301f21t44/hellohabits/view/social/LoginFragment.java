package com.github.cmput301f21t44.hellohabits.view.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.auth.Authentication;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private Authentication auth;
    private FirebaseAuth mAuth;
    private NavController mNav;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mNav = NavHostFragment.findNavController(this);
        auth = new Authentication();
        binding.login.setOnClickListener(v -> {
            String name = binding.name.getText().toString();
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            auth.signIn(email, password)
                    .addOnSuccessListener(authResult -> mNav.navigate(R.id.TodaysHabitsFragment))
                    .addOnFailureListener(e -> {
                        auth.signup(name, email, password);
                        mNav.navigate(R.id.TodaysHabitsFragment);
                    });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.TodaysHabitsFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.TodaysHabitsFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}