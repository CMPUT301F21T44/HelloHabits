package com.github.cmput301f21t44.hellohabits.view;

import android.widget.EditText;

import java.util.Arrays;

public abstract class InputValidator {
    /**
     * This function returns a result whether there is a empty input
     *
     * @param editTexts the editText to check validity
     * @return a boolean value for empty input: true for empty,false for not empty
     */
    public static boolean hasEmptyInput(EditText... editTexts) {
        return Arrays.stream(editTexts).anyMatch(e -> e.getText().toString().isEmpty());
    }
}
