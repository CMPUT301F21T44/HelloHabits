package com.github.cmput301f21t44.hellohabits.view;

import android.widget.EditText;

import java.util.Arrays;

public abstract class InputValidator {
    public static boolean hasEmptyInput(EditText... editTexts) {
        return Arrays.stream(editTexts).anyMatch(e -> e.getText().toString().isEmpty());
    }
}
