package com.github.cmput301f21t44.hellohabits.view.habit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.github.cmput301f21t44.hellohabits.R;

/**
 * Dialog Fragment for scheduling the days of week for a Habit
 */
public class DaysOfWeekFragment extends DialogFragment {
    OnConfirmCallback mConfirmCallback;
    private boolean[] mSelectedItems;

    /**
     * This function returns a new instance of the days of week fragment
     *
     * @param days     a boolean array of days
     * @param callback Callback for when the input is confirmed
     * @return a new instance of the days of week fragment
     */
    public static DaysOfWeekFragment newInstance(boolean[] days, OnConfirmCallback callback) {
        DaysOfWeekFragment newFragment = new DaysOfWeekFragment();
        newFragment.mSelectedItems = days;
        newFragment.mConfirmCallback = callback;
        return newFragment;
    }

    /**
     * Create a new fragment of days of week for user to select days for habit event
     *
     * @param savedInstanceState a default Bundle
     * @return the built up days of week fragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Reminders");

        builder.setMultiChoiceItems(R.array.days_in_week, mSelectedItems,
                (dialog, which, isChecked) -> {
                    if (isChecked) {
                        mSelectedItems[which] = true;
                    } else if (mSelectedItems[which]) {
                        mSelectedItems[which] = false;
                    }
                });

        builder.setPositiveButton("OK", (dialog, which) ->
                mConfirmCallback.onConfirm(mSelectedItems));

        builder.setNegativeButton("Cancel", null);

        return builder.create();
    }

    /**
     * Interface for the listener callback
     */
    public interface OnConfirmCallback {
        void onConfirm(boolean[] days);
    }
}
